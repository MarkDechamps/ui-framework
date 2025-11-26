(function(){
  var overlay = document.getElementById('lookup-overlay');
  var modal = document.getElementById('lookup-modal');
  var inputEl = document.getElementById('lookup-input');
  var listEl = document.getElementById('lookup-list');
  var closeBtns = [];

  // Failsafe: ensure overlay and modal start hidden even if cached CSS is stale
  if (overlay) { overlay.hidden = true; overlay.setAttribute('aria-hidden','true'); }
  if (modal)   { modal.hidden   = true; modal.setAttribute('aria-hidden','true'); }

  function qAll(sel){ return Array.prototype.slice.call(document.querySelectorAll(sel)); }
  function show(el){ el.hidden = false; el.setAttribute('aria-hidden','false'); }
  function hide(el){ el.hidden = true; el.setAttribute('aria-hidden','true'); }

  var currentCtx = null; // { url, wrapper, codeInput, hidden, nameSpan }

  function openModal(ctx){
    currentCtx = ctx;
    if (!inputEl || !overlay || !modal) return;
    inputEl.value = (ctx.codeInput && ctx.codeInput.value) || '';
    // Wanneer htmx aanwezig is, zorg dat ref wordt meegegeven bij requests
    try {
      if (window.htmx && ctx.ref) {
        var vals = { ref: ctx.ref };
        inputEl.setAttribute('hx-vals', JSON.stringify(vals));
      }
    } catch(e) {}
    renderList([]);
    show(overlay); show(modal);
    setTimeout(function(){ try { inputEl.focus(); inputEl.select(); } catch(e){} }, 0);
    // lock body scroll while modal open
    try { document.body.style.overflow = 'hidden'; } catch(e) {}
    // Kick initial load
    fetchAndRender(inputEl.value);
  }

  function closeModal(){
    if (!overlay || !modal) return;
    hide(modal); hide(overlay);
    // restore body scroll
    try { document.body.style.overflow = ''; } catch(e) {}
    if(currentCtx && currentCtx.codeInput){ try { currentCtx.codeInput.focus(); } catch(e){} }
    currentCtx = null;
  }

  function renderList(items){
    if (!listEl) return;
    listEl.innerHTML = '';
    items.forEach(function(it){
      var li = document.createElement('li');
      li.className = 'lookup-item';
      li.setAttribute('role','option');
      li.tabIndex = 0;
      li.dataset.id = it.id || '';
      li.dataset.code = it.code || '';
      li.dataset.name = it.name || '';
      li.textContent = (it.code ? it.code + ' — ' : '') + (it.name || '');
      // single click select (was dblclick)
      li.addEventListener('click', function(){ selectItem(li); });
      li.addEventListener('keydown', function(ev){ if(ev.key === 'Enter'){ selectItem(li); } });
      listEl.appendChild(li);
    });
  }

  function fetchAndRender(query){
    if(!currentCtx || !currentCtx.url) return;
    var url = currentCtx.url + '?code=' + encodeURIComponent(query || '');
    fetch(url)
      .then(function(r){ return r.ok ? r.json() : []; })
      .then(function(list){ renderList(Array.isArray(list) ? list : []); })
      .catch(function(){ renderList([]); });
  }

  // Debounce only when htmx is NOT present; with htmx we use hx-trigger on the input
  var debTimer = null;
  if (inputEl && !window.htmx) {
    inputEl.addEventListener('input', function(){
      clearTimeout(debTimer);
      var q = this.value;
      debTimer = setTimeout(function(){ fetchAndRender(q); }, 180);
    });
  }

  function selectItem(li){
    if(!currentCtx) return;
    var code = li.dataset.code || '';
    var name = li.dataset.name || '';
    var id = li.dataset.id || code || '';
    if(currentCtx.codeInput) currentCtx.codeInput.value = code;
    if(currentCtx.nameSpan) currentCtx.nameSpan.textContent = name;
    if(currentCtx.hidden) currentCtx.hidden.value = id;
    closeModal();
  }

  document.addEventListener('click', function(e){
    var btn = e.target.closest && e.target.closest('button.btn.icon[data-lookup-url]');
    if(btn){
      var wrapper = btn.closest('.reference-field');
      if(!wrapper) return;
      var codeInput = wrapper.querySelector('input.input[data-lookup-url]') || wrapper.querySelector('input.input');
      var nameSpan = wrapper.querySelector('.reference-name');
      var hidden = wrapper.querySelector('input[type="hidden"]');
      var url = btn.getAttribute('data-lookup-url') || (codeInput && codeInput.getAttribute('data-lookup-url'));
      var refName = (btn.getAttribute('data-ref-code') || (codeInput && codeInput.getAttribute('data-ref-code')) || '').trim();
      if(url){
        openModal({ url: url, wrapper: wrapper, codeInput: codeInput, nameSpan: nameSpan, hidden: hidden, ref: refName });
        // If htmx is present, trigger an initial fetch via htmx (so server renders <li> items)
        if (window.htmx && inputEl) {
          try {
            // ensure input carries the current value under name="code"
            if (!inputEl.name) inputEl.name = 'code';
            // hx-vals wordt in openModal gezet; forceer changed trigger met huidige waarde
            // Fire the htmx trigger used in hx-trigger
            window.htmx.trigger(inputEl, 'changed');
          } catch(e) {}
        } else {
          // Fallback: manual fetch using JSON endpoint
          fetchAndRender(inputEl ? inputEl.value : '');
        }
      }
    }
  });

  // Delegate clicks/Enter on server-rendered <li.lookup-item> (from htmx) to selection
  if (listEl) {
    listEl.addEventListener('click', function(e){
      var li = e.target.closest && e.target.closest('li.lookup-item');
      if (!li) return;
      // Wanneer htmx aanwezig is en dit item een hx-post draagt, laat htmx het afhandelen (geen dubbele select)
      if (window.htmx && (li.getAttribute('hx-post') || li.getAttribute('data-hx-post'))) return;
      selectItem(li);
    });
    listEl.addEventListener('keydown', function(e){
      if (e.key === 'Enter') {
        var li = e.target.closest && e.target.closest('li.lookup-item');
        if (!li) return;
        if (window.htmx && (li.getAttribute('hx-post') || li.getAttribute('data-hx-post'))) return;
        selectItem(li);
      }
    });
  }

  // Close interactions
  closeBtns = qAll('.lookup-close, .lookup-cancel');
  closeBtns.forEach(function(b){ b.addEventListener('click', closeModal); });
  if (overlay) overlay.addEventListener('click', closeModal);
  document.addEventListener('keydown', function(e){ if(e.key === 'Escape' && modal && !modal.hidden){ closeModal(); } });

  // Submit handler: build DTO and log to console (demo)
  var form = document.querySelector('form.form-body');
  if(form){
    form.addEventListener('submit', function(ev){
      ev.preventDefault();
      var dto = {};
      var fields = Array.prototype.slice.call(form.querySelectorAll('input, select, textarea'));
      var handledNames = new Set();
      fields.forEach(function(el){
        if(el.type === 'hidden'){
          var name = el.name; // e.g. "postcode"
          var wrapper = el.closest('.reference-field');
          if(wrapper){
            var codeInput = wrapper.querySelector('input.input[name="'+name+'_code"]')
                           || wrapper.querySelector('input.input[data-lookup-url]');
            var nameSpan = wrapper.querySelector('.reference-name');
            dto[name] = {
              id: el.value || '',
              code: codeInput ? codeInput.value : '',
              name: nameSpan ? nameSpan.textContent : ''
            };
            handledNames.add(name);
            handledNames.add(name + '_code');
          }
        }
      });
      fields.forEach(function(el){
        if(!el.name || handledNames.has(el.name)) return;
        if(el.type === 'hidden') return;
        var val = el.value;
        dto[el.name] = val;
      });
      try {
        console.log('DTO submitted:', dto);
        console.log('DTO (json):', JSON.stringify(dto, null, 2));
      } catch(e) {
        console.log('DTO submitted (raw):', dto);
      }
    });
  }
})();
