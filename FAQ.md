# Advanced JSON Studio — FAQ

A comprehensive guide to everything the plugin can do, how to use it, and how to get help when something goes wrong.

---

## Table of Contents

- [General](#general)
- [Opening the Plugin](#opening-the-plugin)
- [Input & Supported Formats](#input--supported-formats)
- [Parsing & Formatting](#parsing--formatting)
- [Tabs & Sessions](#tabs--sessions)
- [Output Tabs & Conversions](#output-tabs--conversions)
- [Flatten & Unflatten](#flatten--unflatten)
- [CSV View](#csv-view)
- [Base64 Encode](#base64-encode)
- [Schema Validator](#schema-validator)
- [Rich Editor Decorations](#rich-editor-decorations)
- [Tree View](#tree-view)
- [Graph View](#graph-view)
- [Query (JSONPath & JMESPath)](#query-jsonpath--jmespath)
- [JSON Diff](#json-diff)
- [JWT Decode & Verify](#jwt-decode--verify)
- [Code Generation](#code-generation)
- [Dummy JSON Generator](#dummy-json-generator)
- [AI-Powered Repair](#ai-powered-repair)
- [Privacy Mode](#privacy-mode)
- [Loading JSON from Files & URLs](#loading-json-from-files--urls)
- [HTTP Client & cURL](#http-client--curl)
- [Open in Editor](#open-in-editor)
- [Inline JSON Hints](#inline-json-hints)
- [Settings & Customization](#settings--customization)
- [Localization](#localization)
- [Debugging & Troubleshooting](#debugging--troubleshooting)
- [Reporting Issues & Feature Requests](#reporting-issues--feature-requests)
- [Freemium & Pricing](#freemium--pricing)
- [Supporting the Project](#supporting-the-project)

---

## General

**What is Advanced JSON Studio?**
Advanced JSON Studio is a full-featured JSON engineering workspace built into IntelliJ-based IDEs (IntelliJ IDEA, Android Studio, WebStorm, PyCharm, etc.). It goes far beyond formatting — it lets you parse, repair, convert, query, visualize, diff, generate code from, and decode JSON and related data formats directly inside your IDE.

**Which IDEs are supported?**
Any IntelliJ Platform IDE (2023.1+), including IntelliJ IDEA, Android Studio, WebStorm, PhpStorm, PyCharm, GoLand, CLion, and Rider.

**Is the plugin free?**
The core features (parsing, tree view, basic search) remain free. Advanced and newly added features are moving to a freemium model with a generous daily free usage allowance. See [Freemium & Pricing](#freemium--pricing) for details.

**Where can I find the plugin?**
Search for **"Advanced JSON Studio"** in `Settings` → `Plugins` → `Marketplace`, or visit the [JetBrains Marketplace page](https://plugins.jetbrains.com/plugin/10650-json-parser).

---

## Opening the Plugin

**How do I open the JSON Studio tool window?**
Click the **JSON Studio** panel in your IDE's right or bottom tool window strip. If you don't see it, go to `View` → `Tool Windows` → `JSON Studio`.

**How do I open a JSON string from code directly in the parser?**
Select any JSON string in the editor, then press `Ctrl+Shift+Alt+J` (or use `Alt+Enter` → **Open in JSON Parser** from the intention menu). The string opens in a new parser tab automatically.

**How do I open a JSON file from the Project panel?**
Right-click the `.json` file in the Project panel → **Open In** → **JSON Parser**.

**Can I open the parser as a full editor tab instead of a tool window?**
Yes. Click the **Open in Editor** button in the toolbar. The current tab's name and content are carried over and you get the full editor area.

---

## Input & Supported Formats

**What formats can I paste or type into the input area?**
The plugin auto-detects and handles:
- **JSON** and **JSON5** (unquoted keys, single quotes, trailing commas, comments)
- **YAML**
- **XML**
- **TOML**
- **.properties**
- **JWT tokens**
- **CBOR** and **BSON** (binary hex input)

**Do I need to tell the plugin what format I'm pasting?**
No. The format is detected automatically as you type or paste. A label at the bottom confirms the detected format.

**Does the plugin support JSON5?**
Yes. JSON5 features (unquoted keys, single-quoted strings, trailing commas, comments) are parsed correctly.

**Can I paste YAML and have it converted to JSON?**
Yes. Paste YAML in the input — it is parsed and displayed as formatted JSON in the Pretty tab automatically.

**What is the input size limit?**
There is no hard limit enforced by the plugin. Very large files may feel slow depending on your machine.

---

## Parsing & Formatting

**Do I need to click Parse every time?**
No. The plugin auto-parses as you type — the output updates live without pressing any button.

**How do I pretty-print JSON?**
It happens automatically. The **Pretty** output tab always shows formatted JSON. You can change the indentation size in **Parser Settings**.

**How do I minify JSON to a single line?**
Click the **Minify** output tab. The minified JSON is shown there and can be copied or saved.

**Can I sort JSON keys alphabetically?**
Yes. Go to **Parser Settings** (gear icon in the toolbar) and enable **Sort keys alphabetically**. Sorting applies recursively to all nested objects.

**How do I change the indentation size?**
Open **Parser Settings** → **Indentation size (spaces)** → pick 1–8.

**Where can I see stats like key count, depth, and file size?**
The statistics bar at the bottom of the output panel shows: **Keys, Depth, Objects, Arrays, Nulls, and Size** — updated live on every parse.

---

## Tabs & Sessions

**How do I add a new tab?**
Click the **+** (Add Tab) button in the toolbar, or use the **Add Tab** action from the sidebar.

**How do I close a tab?**
Click the **✕** icon on the tab label. The last tab cannot be closed.

**How do I rename a tab?**
Double-click the tab label. A dialog appears — enter a name between 2 and 30 characters and press OK.

**Are my sessions saved when I close the IDE?**
Yes. Up to 10 tab sessions (input content and tab names) are saved locally and restored automatically on restart. No cloud sync — everything stays on your machine.

**How do I clear all saved session data?**
Go to **Parser Settings** → **Clear All Saved JSON Data**. This resets all tabs and clears persisted content. This action cannot be undone.

**Is there a limit to how many tabs I can open?**
You can open up to 10 tabs. The Add Tab button is hidden once you reach that limit.

---

## Output Tabs & Conversions

**What output formats are available?**
The output panel has the following tabs:

| Tab | Description |
|---|---|
| **Pretty** | Formatted, indented JSON |
| **Minify** | Single-line JSON |
| **YAML** | Converted to YAML |
| **XML** | Converted to XML |
| **TOML** | Converted to TOML (`[[table]]` syntax for arrays of objects) |
| **Properties** | Converted to `.properties` key=value format |
| **CBOR** | Binary CBOR encoding shown as hex |
| **BSON** | Binary BSON encoding shown as hex |
| **Schema** | Auto-inferred JSON Schema |
| **Flatten** | Flattens nested JSON to dot-notation keys |
| **Unflatten** | Rebuilds nested JSON from dot-notation keys |
| **CSV** | Renders an array of objects as an interactive table |
| **Base64** | Minifies JSON and encodes it as a Base64 string |
| **Tree** | Interactive tree view |
| **Graph** | Node-graph visualisation |
| **Query** | JSONPath / JMESPath query engine |
| **JWT** | JWT decode and signature verification |

**Do you support XML conversion?**
Yes. Click the **XML** output tab — your JSON is converted to XML instantly.

**Do you support TOML?**
Yes. Arrays of objects use correct `[[table]]` array-of-tables syntax. Root-level arrays are wrapped under the key `items` (TOML does not support bare root arrays by spec).

**Do you support CBOR / BSON?**
Yes. Both binary formats are shown as a hex dump in their respective output tabs.

**Can I save the converted output to a file?**
Yes. Each output tab has a **Save to Local** button in its toolbar.

**Do you support JSON Schema generation?**
Yes. The **Schema** tab auto-infers a JSON Schema from your input structure instantly.

---

## Flatten & Unflatten

**What does Flatten do?**
The **Flatten** tab converts a deeply nested JSON object into a single-level object where each key is the full dot-notation path to the original value. Array indices use bracket notation (e.g. `address.phones[0].number`).

**Example:**
```json
{ "user": { "name": "Alice", "address": { "city": "London" } } }
```
becomes:
```json
{ "user.name": "Alice", "user.address.city": "London" }
```

**What does Unflatten do?**
The **Unflatten** tab is the inverse — it takes a flat dot-notation object and reconstructs the original nested structure.

**When would I use this?**
Flattening is useful when comparing two deeply nested documents, inserting values into flat key-value stores (Redis, config files), or debugging path resolution issues.

---

## CSV View

**What is the CSV tab?**
When your JSON input is an array of objects (a common API response shape), the **CSV** tab renders it as an interactive table with auto-sized columns and alternating row colors.

**Can I download the table as a CSV file?**
Yes. Click the **Download** button in the CSV tab toolbar. The file is exported in RFC-4180 compliant format with a header row and proper quoting.

**What happens if my JSON is not an array of objects?**
The CSV tab shows a message explaining that the format is not supported. It expects a top-level JSON array where each element is a JSON object.

---

## Base64 Encode

**What does the Base64 tab do?**
The **Base64** tab minifies your JSON (removes all whitespace) and encodes it as a Base64 string — useful for embedding JSON in URLs, HTTP headers, or config files that require Base64.

**Does the plugin also decode Base64?**
Yes — automatically. If you paste a Base64 string into the input area, the parser detects it, decodes it, and processes the embedded JSON transparently. You will see **Base64** shown as the detected format label. No separate decode step is needed.

---

## Schema Validator

**What is the Schema Validator?**
The **Schema Validator** tab lets you validate your JSON input against any JSON Schema (Draft 4/6/7/2019-09/2020-12). You write or paste a schema in the top editor; the results appear in the bottom editor automatically as you type.

**Do I have to click a Validate button?**
No. Validation is fully automatic — it re-runs on every change to either the JSON input or the schema, with a short debounce delay.

**How are errors shown?**
There are two view modes, toggled by the icons in the tab toolbar:

- **List mode** — errors are listed as text lines in the results editor with path and message.
- **Annotated mode** — the results editor shows the pretty-printed JSON with inline error hints displayed after the relevant line (virtual text, no document modification) and a colored background highlight on affected lines.

**Is the schema saved between sessions?**
Yes. The last schema you entered is saved automatically and restored when you reopen the tab.

**Can I manage multiple schemas?**
Yes. Use the dropdown at the top of the tab to switch between saved schemas. The **+** button saves the current schema under a new name, and the trash icon deletes the selected entry.

---

## Rich Editor Decorations

**What are Rich Editor Decorations?**
When enabled, the **Pretty** output tab shows additional visual hints in the gutter and inline — these help you understand values at a glance without leaving the plugin.

**What decorations are available?**

| Decoration | Trigger | Where shown |
|---|---|---|
| Color swatch | Hex `#rrggbb`, `rgb()`, `hsl()` values | Gutter icon |
| Image thumbnail | Image URLs and `data:image/…` URIs | Gutter icon (clickable → popup preview) |
| Link icon | `https://` / `http://` URLs | Gutter icon |
| Clock icon | ISO datetime strings, Unix timestamps | Gutter icon |
| Calendar icon | Date-only strings (`2024-01-15`) | Gutter icon |
| UUID badge | UUID strings | Gutter icon |
| Human timestamp | Unix timestamps | Inline hint (formatted date) |
| File size | Fields named `size`, `bytes`, `filesize` | Inline hint (e.g. `1.2 MB`) |
| Duration | Fields named `duration`, `millis`, `ttl` | Inline hint (e.g. `3m 20s`) |
| Large number | Any large numeric value | Inline hint (formatted with commas) |

**How do I toggle individual decorations?**
Each decoration type has its own toggle in **Parser Settings** under the **Rich Editor** section.

**Is Rich Editor a Pro feature?**
Yes. Rich Editor decorations are gated under the Pro plan, with a daily free usage allowance so you can try them out.

---

## Privacy Mode

**What is Privacy Mode?**
Privacy Mode is a per-session toggle (shield icon in the toolbar) that ensures **no data leaves your machine** for that session. When active, a green animated border draws around the parser widget as a clear visual indicator.

**What does Privacy Mode block?**
- AI-powered JSON repair (cloud step only — local heuristic repair still runs fully offline)
- Cloud-based code generation

**Is Privacy Mode on by default?**
No. It is off by default and resets to off every time you restart the IDE — it is a session-only safeguard, never persisted to disk.

**How is Privacy Mode different from disabling AI repair in Settings?**
Settings changes are permanent and global. Privacy Mode is a quick, temporary, per-session lock — flip it on when working with sensitive data without changing your permanent settings.

---

## HTTP Client & cURL

**What is the cURL tab in the HTTP client?**
The HTTP client dialog has a **cURL** tab where you can paste any curl command directly. The plugin parses it locally and populates the URL, method, headers, and body fields automatically — no shell process is ever invoked, so there is no command injection risk.

**Which curl flags are supported?**

| Flag | Effect |
|---|---|
| `-X` / `--request` | Sets the HTTP method |
| `-H` / `--header` | Adds a request header |
| `-d` / `--data` / `--data-raw` / `--data-binary` | Sets the request body |
| `-u` / `--user` | Encodes credentials as a Basic Auth header |
| `-L`, `-s`, `-v`, `--compressed`, `--insecure` | Silently ignored (safe flags) |

**Does the cURL parser handle multi-line curl commands?**
Yes. Backslash line continuation (`\` at end of line) is handled correctly, so commands copied from documentation or terminal history work as-is.

**Does it support quoted strings and escaped characters?**
Yes. Single-quoted strings are treated literally; double-quoted strings handle `\"` and `\\` escapes — matching standard shell quoting rules.

---

## Tree View

**How do I use the Tree View?**
Click the **Tree** output tab. Your JSON is rendered as an expandable/collapsible tree.

**Can I search inside the tree?**
Yes. A search bar at the top of the Tree tab filters and highlights matching keys and values live as you type.

**How do I copy a JSONPath from the tree?**
Click any node in the tree — the JSONPath for that node is shown below the tree and can be copied with one click.

---

## Graph View

**How do I open the Graph View?**
Click the **Graph** output tab. Your JSON structure is rendered as an interactive node-graph.

**The graph is too small — can I expand it?**
Yes. Click the **Expand** button to open the graph in a full-screen dialog.

---

## Query (JSONPath & JMESPath)

**How do I run a JSONPath or JMESPath query?**
Click the **Query** output tab. Type your expression in the query field and press **Run** (or Enter). Results appear immediately.

**Does autocomplete work in the query field?**
Yes. As you type, suggestions are drawn from the actual keys in your current JSON input.

**Where can I learn the query syntax?**
Click the **?** (syntax help) button next to the query field for an inline reference with examples for both JSONPath and JMESPath.

**Which query languages are supported?**
Both **JSONPath** and **JMESPath** are supported in the same query tab.

---

## JSON Diff

**How do I compare two JSON documents?**
Click the **JSON Diff** button in the toolbar. The left side is pre-filled with your current tab's content. Paste or type the second JSON on the right side. Differences are highlighted inline using IntelliJ's native diff engine (same as the git merge view).

**Does the diff tool format pasted JSON automatically?**
Yes. JSON pasted on the right side is auto-formatted before the diff is computed.

---

## JWT Decode & Verify

**Does the plugin support JWT tokens?**
Yes. Paste any JWT token into the input — the **JWT** output tab automatically decodes the header and payload as formatted JSON.

**Can I verify a JWT signature?**
Yes. In the JWT tab, enter your HMAC secret or PEM public key in the key field and click **Verify Signature**. The result is shown with a colour-coded status (✓ valid / ✗ invalid).

**Which signature algorithms are supported?**
- **HMAC**: HS256, HS384, HS512
- **RSA**: RS256, RS384, RS512
- **ECDSA**: ES256, ES384, ES512

---

## Code Generation

**Can the plugin generate code from JSON?**
Yes. Click **Generate Code** in the toolbar (or the **Generate Code from JSON** action). Select a language, enter a root type name, and click **Generate**.

**Which languages are supported for code generation?**
TypeScript, JavaScript, TypeScript + Zod, Python, Java, **Kotlin**, Swift, Go, C#, Rust, Ruby, and **Dart** — powered by the **Quicktype** engine.

**Where is the generated code saved?**
A file picker lets you choose any directory inside your project. The file is written directly into the project.

**Can I generate Dart freezed or data class models?**
Yes. Dart generation includes correct `_$ClassName` mixin and `fromJson` generation for freezed-style classes.

**Can I generate Kotlin data classes?**
Yes. Kotlin code generation produces idiomatic data classes from your JSON structure.

**Does code generation send my JSON to the cloud?**
No — your actual JSON data never leaves your machine. The plugin parses your JSON locally and extracts only the **schema structure** (field names and types). That schema is what gets sent to the cloud for code generation. Your values, your data, and your business logic stay entirely on your system.

**Does code generation require an internet connection?**
Yes. The schema-to-code step is cloud-based (Quicktype API) and requires an active internet connection.

---

## Dummy JSON Generator

**How do I generate fake/test JSON?**
Click the **Generate Dummy JSON** button in the toolbar. Configure:
- **Type**: Object or Array
- **Array size**: number of items
- **Properties per object**: 1–100
- **Nesting depth**: 1–10

The generated JSON opens in a new parser tab.

---

## AI-Powered Repair

**What does the Repair button do?**
Clicking **Repair** attempts to fix malformed JSON using built-in heuristics. If those fail, it can optionally use an AI strategy. Fixed content is shown in a merge diff so you can review and accept changes.

**What kinds of errors can it fix?**
Missing quotes around keys, missing or extra commas, mismatched brackets, trailing commas, and other common structural mistakes.

**Does AI repair send my JSON to a server?**
When AI repair is enabled, your JSON is sent to a **private cloud** exclusively for the repair operation. Running a capable AI repair model locally is not feasible — the model size would make the plugin impractical to install for everyone. To protect your privacy:

- Your data is **never logged**, **never stored**, and **never used** to train any AI model.
- The data is processed in transit solely to produce a repaired result and is discarded immediately.
- It is **completely safe** — but if you still prefer to keep all data local, you can disable the feature entirely (see below).

Built-in heuristic repair (missing quotes, commas, brackets) always runs **100% offline** regardless of this setting.

**How do I disable AI repair?**
Go to `Settings` → `Json Studio` and uncheck **Enable fixing your JSON using AI**. Only the offline heuristic repair will run after that.

**What happens if repair fails?**
A notification is shown: _"This JSON can't be fixed"_. If error tracking is enabled in settings, the failing JSON string may be logged anonymously to help diagnose edge cases — you can disable that too from the same settings page.

---

## Loading JSON from Files & URLs

**How do I load a JSON file from disk?**
Right-click in the input area → **Load from File**, or click the **Load from File** button in the toolbar. A file picker opens at your project root.

**How do I fetch JSON from a URL?**
Right-click in the input area → **Retrieve content from URL**, or click **Load from URL** in the toolbar. An HTTP client dialog opens where you can set the URL, method, custom headers, and a request body.

**Which HTTP methods are supported?**
GET, POST, PUT, and PATCH — all with optional request body support.

**Can I add custom request headers?**
Yes. The HTTP client dialog has a headers table — click **Add** to insert key-value header rows.

---

## Open in Editor

**How do I pop the parser into a full editor tab?**
Click the **Open in Editor** button in the toolbar. The current tab's name and JSON content are carried over into a new standalone editor tab.

**Do all features work in the editor tab?**
Yes. Input, parse, repair, code generation, and all output tabs work the same in the editor tab mode.

---

## Inline JSON Hints

**What are inline JSON hints?**
When a line of code contains an embedded JSON string (single-line or triple-quoted), an inlay hint appears inline with a link to **Open in JSON Parser**. This lets you inspect or edit the JSON without copying it manually.

**How do I use the intention action?**
Place your cursor on a line with a JSON string, press `Alt+Enter`, and choose **Open in JSON Parser**.

**How do I disable inline hints?**
Go to **Parser Settings** → uncheck **Show inline JSON hints**.

---

## Settings & Customization

**Where are the plugin settings?**
There are two settings locations:
1. **Parser Settings** (gear icon in the toolbar) — indentation, key sorting, tips, saved data.
2. `Settings` → `Json Studio` — analytics, error tracking, AI repair toggle.

**What can I configure in Parser Settings?**

| Setting | Description |
|---------|-------------|
| Indentation size | 1–8 spaces |
| Sort keys alphabetically | Sorts all keys recursively |
| Show tips of the day | Toggle the tip bubble |
| Clear All Saved JSON Data | Wipes all persisted tab sessions |

**How do I disable the Tip of the Day?**
Click **Don't show again** on the tip bubble, or uncheck **Show tips of the day** in Parser Settings.

---

## Localization

**Is the plugin available in other languages?**
Yes. The entire UI is localized into 13 languages in addition to English:

Japanese, Korean, Chinese (Simplified), German, French, Spanish, Italian, Portuguese (Brazilian), Russian, Polish, Dutch, Turkish, Czech.

**How do I change the plugin language?**
The plugin follows your IDE locale automatically. Change your IDE language under `Settings` → `Appearance & Behavior` → `System Settings` → `Language` and restart the IDE.

---

## Debugging & Troubleshooting

**The output is not updating — what should I do?**
1. Make sure the input field is focused and you have typed or pasted content.
2. Check that the JSON is not completely empty.
3. Try clicking the **Parse** button manually.
4. Restart the IDE and try again.

**My JSON is showing an error but I believe it's valid — why?**
The plugin may be receiving JSON5, YAML, or another format. Check the detected format label at the bottom. If the format label is wrong, try clicking **Repair** which applies a more permissive parser.

**The tab rename dialog won't let me confirm — why?**
Tab names must be between 2 and 30 characters. The OK button stays disabled until the name meets that requirement.

**Sessions are not restored after restart — what happened?**
Session data may have been cleared manually, or the IDE crashed before the data was saved. Go to **Parser Settings** → check **Saved session data** to see if any data exists.

**The code generation failed — what should I check?**
1. Make sure your JSON input is valid (the Pretty tab should show formatted output without errors).
2. Check your internet connection — code generation requires network access.
3. Make sure a language is selected and the type name field is not empty.

**The HTTP client returned an error — what should I check?**
1. Confirm the URL is reachable from your machine.
2. Check that the URL starts with `https://` or `http://`.
3. If the server requires authentication, add the appropriate `Authorization` header.

**Where can I find plugin logs?**
Go to `Help` → `Show Log in Finder/Explorer` in your IDE to open the log directory. Search for `JsonParser` or `JsonStudio` entries.

**The plugin is not appearing in the tool window strip — what should I do?**
Go to `View` → `Tool Windows` → `JSON Studio`. If it is not listed, try reinstalling the plugin from `Settings` → `Plugins`.

---

## Reporting Issues & Feature Requests

**How do I report a bug?**
Open an issue on [GitHub](https://github.com/godwinjk/Json_Parser/issues) with:
- Your IDE name and version
- Plugin version (visible in `Settings` → `Plugins`)
- Steps to reproduce
- The JSON input that caused the issue (if applicable and not sensitive)

**How do I request a new feature?**
Open a [GitHub issue](https://github.com/godwinjk/Json_Parser/issues) with the label `enhancement`. Describe the use case clearly — what problem it solves and what you expect the behavior to be.

**Do you have a feature X?**
Check the [feature list in the README](README.md#features) first. If it's not listed, search [open issues](https://github.com/godwinjk/Json_Parser/issues) to see if it has been requested. If not, open a new issue.

---

## Freemium & Pricing

**What features are free?**
- Core JSON parsing and pretty-printing
- Basic tree view and search
- Basic format detection

**What features require a subscription?**
Advanced export options and high-performance graph layout algorithms for massive datasets are moving to premium. A generous daily free usage allowance is provided so casual and student users are never completely blocked.

**Will existing features be taken away?**
No. Features you already use remain accessible. The freemium model applies to new and advanced features going forward.

---

## Supporting the Project

Advanced JSON Studio is built and maintained by a single developer. If it saves you time every day, please consider:

- ⭐ Leaving a [review on the JetBrains Marketplace](https://plugins.jetbrains.com/plugin/10650-json-parser)
- ❤️ [Donating via PayPal](https://paypal.me/godwinj)
- ☕ [Buying a coffee on Ko-fi](https://ko-fi.com/S6S0176OVQ)

Every bit of support helps keep the plugin bug-free, modern, and free for the community.
