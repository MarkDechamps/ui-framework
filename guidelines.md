# UI Framework Guidelines

These guidelines describe conventions and best practices for this project. They cover templates (Thymeleaf), frontend behavior for the lookup modal, CSS, testing, and general coding style.

## 1) Templates (Thymeleaf)

- Always use wrapped fragment expressions with the `~{...}` syntax.
  - Correct:
    - `th:replace="~{layout :: layout}"`
    - `th:replace="~{fragments/fields :: fields(${form})}"`
  - Avoid deprecated unwrapped forms like `layout :: layout` without `~{}`.
- Prefer `th:insert="~{::content}"` in the layout to place child content where appropriate.
- Keep templates lean; business logic belongs in Java. Use Thymeleaf only for simple conditionals/loops and attribute/fragment composition.
- Escape dynamic values by default (Thymeleaf does this for text). Only use `th:utext` for vetted, safe snippets (e.g., inline SVGs), never for user-provided data.

## 2) Lookup Modal UX/JS

- The modal overlay and dialog must be present in the main screen template and be hidden by default using the HTML `hidden` attribute:
  - `#lookup-overlay` and `#lookup-modal` have `hidden` initially and `aria-hidden="true"`.
- Show/hide utilities must toggle both `hidden` and `aria-hidden`:
  - Show: `el.hidden=false; el.setAttribute('aria-hidden','false')`
  - Hide: `el.hidden=true;  el.setAttribute('aria-hidden','true')`
- Item selection uses single click and Enter key:
  - Each `li.lookup-item` binds `click` to select; `keydown(Enter)` selects as well.
- Opening the modal should:
  - Focus the search input and select its content.
  - Lock body scroll: `document.body.style.overflow='hidden'`.
  - Kick off an initial fetch using the current code input value.
- Closing the modal should:
  - Restore body scroll and focus the originating code input.
  - Re-hide overlay and modal with `hidden` and `aria-hidden`.

### Defensive startup

- Add a quick initialization step that explicitly sets the modal and overlay to hidden. This guards against stale caches or CSS not yet applied:
```
if (overlay) { overlay.hidden = true; overlay.setAttribute('aria-hidden','true'); }
if (modal)   { modal.hidden   = true; modal.setAttribute('aria-hidden','true'); }
```

## 3) CSS Conventions

- Include a global rule so the `hidden` attribute always takes effect:
```
[hidden]{
  display: none !important;
}
```
- Use CSS variables where helpful, and keep components self-contained (e.g., `.lookup-modal`, `.lookup-overlay`).
- For cache-busting, link stylesheets with a version query param from templates and fallback renderer, e.g. `/css/styles.css?v=20251126`.

## 4) Accessibility (A11y)

- Use `role="dialog"` and `aria-modal="true"` on the modal, with `aria-labelledby` pointing to the title element.
- Toggle `aria-hidden` alongside `hidden`.
- Ensure focus management: focus the input on open; restore focus to the trigger on close.
- Ensure list items are keyboard accessible: `tabIndex=0` and `keydown(Enter)` to select.

## 5) Testing Strategy

- Controller rendering tests: use `MockMvc` to request HTML and assert the presence of fields and modal elements.
- Template fallback tests: mock the `TemplateEngine` to throw, then assert the "simple HTML" path produces expected markup.
- Modal structure tests (server-side): ensure `#lookup-overlay` and `#lookup-modal` exist and are hidden by default; verify `data-lookup-url` attributes exist on reference controls.
- End-to-end (optional): Selenium/WebDriver can be used to verify click interactions if dependency resolution is available in CI.

## 6) Java Coding Style

- Keep UI composition in `ThymeleafRenderer` and templates. Business/data shaping belongs in controller/screen classes.
- Follow fluent builder patterns in `FormBuilder` and domain DTOs, maintaining type-safe chaining.
- Log with `slf4j` at appropriate levels; keep default logs quiet during happy-path rendering.

## 7) Reference Fields

- The composite control includes:
  - Visible code input: `name="{fieldName}_code"`
  - Display span for human-readable label: `.reference-name`
  - Hidden input carrying the actual value/ID: `name="{fieldName}"`
  - Search button with `data-lookup-url` (and mirror on code input)
- Selection procedure (in JS): update hidden ID, code input text, and display name; then close the modal.

## 8) Error Handling & Network

- `fetch` to lookup endpoints should handle non-OK responses gracefully and render an empty list.
- Debounce input requests (~180ms) to limit network chatter during typing.

## 9) Upgrading Thymeleaf

- The old unwrapped fragment syntax will be removed in future versions. Always use `~{...}` now to stay forward-compatible.
- Keep OGNL available for Thymeleaf (already declared as a dependency for tests/builds). If removing it, ensure templates do not require OGNL-based features.

## 10) How to add a new screen

1. Create a new DTO in `org.example.dto` and define fields.
2. Create a `Screen` class that assembles a `Form` using `FormBuilder`.
3. Add a controller endpoint that:
   - Instantiates the DTO and Screen
   - Calls the renderer to produce HTML
4. Add tests:
   - Controller test for the endpoint
   - Optional renderer test to verify fallback behavior
5. If the screen needs a reference field, provide a JSON endpoint returning a list of `{id, code, name}` objects.

## 11) Commit Practices

- Keep commits focused: one concern per commit (e.g., template syntax migration, modal behavior tweak, test addition).
- Explain why in messages (e.g., "Migrate to Thymeleaf ~{...} syntax to remove deprecation warnings").

## 12) Clean Code & Object-Oriented Design

- Favor clear, intention-revealing names over comments:
  - Prefer extracting well-named methods/classes over adding explanatory comments.
  - If a comment is needed, consider refactoring first so the code explains itself.
- Apply SOLID principles when designing Java code:
  - Single Responsibility: each class has one purpose (e.g., rendering vs. data shaping).
  - Open/Closed: extend behavior via new types instead of modifying core ones.
  - Liskov Substitution: subclasses/implementations must be substitutable for their base types.
  - Interface Segregation: keep interfaces small and focused.
  - Dependency Inversion: depend on abstractions (e.g., interfaces) where appropriate.
- Encapsulation first:
  - Keep fields private unless exposure is intentional; provide minimal, cohesive APIs.
  - Use immutable objects or final fields where practical to reduce side effects.
- Small, cohesive methods:
  - Aim for one level of abstraction per method and keep cyclomatic complexity low.
  - Extract helper methods with meaningful names rather than adding inline comments.
- Avoid duplication (DRY):
  - Reuse shared utilities and renderer helpers; do not copy/paste HTML/JS generation logic.
- Guardrails in code reviews:
  - Reject identifiers like `doWork()`, `handleStuff()`. Prefer `renderFormBody()`, `openModal()`, `buildSelectOptions()`.
  - Require unit tests for complex logic and when fixing bugs.
  - Enforce consistent formatting and organize imports per project conventions.
