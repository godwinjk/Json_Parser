# Json Parser

![Build](https://github.com/godwinjk/Json_Parser/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/10650-json-parser.svg)](https://plugins.jetbrains.com/plugin/10650-json-parser)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/10650-json-parser.svg)](https://plugins.jetbrains.com/plugin/10650-json-parser)

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/godwinj)  OR 
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S0176OVQ)
# JSON PARSER

<!-- Plugin description -->
JSON Parser is a powerful tool that simplifies working with JSON — from instant parsing and formatting to code generation, querying, diffing, and more.

1. Auto-parse as you type — results update instantly without pressing a button.
2. Present JSON in a clean, readable format with automated pretty printing.
3. Validate JSON for accuracy and integrity with ease.
4. Load local JSON files effortlessly with a dedicated sidebar button.
5. Retrieve JSON from web sources quickly — supports custom headers too.
6. Work across multiple tabs — each session is isolated, close tabs individually.
7. Navigate JSON structures effortlessly using an intuitive tree view.
8. Query JSON using **JSONPath** and **JMESPath** expressions with live autocomplete.
9. Convert JSON to YAML instantly.
10. Infer a JSON Schema from your JSON structure automatically.
11. Minify JSON to a single line with one click.
12. Custom indentation size (1–8 spaces) and alphabetical key sorting.
13. View live statistics — key count, depth, object/array counts, and file size.
14. Compare two JSON documents side-by-side with structural diff highlighting.
15. Generate realistic dummy JSON for testing — choose structure, depth, and size.
16. Repair malformed JSON automatically using built-in and AI-powered strategies.
17. Generate Dart code efficiently and with precision.
18. Create Kotlin code seamlessly using advanced tools.
19. Your JSON input is automatically saved locally and restored on restart — no cloud sync, fully private.

<!-- Plugin description end -->

## Features

- [x] Auto-parse on input change
- [x] Pretty print Json
- [x] Validate Json
- [x] Load Json from local (dedicated sidebar button)
- [x] Retrieve Json from web (dedicated sidebar button)
- [x] JSON data persistance(Data will saved locally)
- [x] Multiple tabs with per-tab close button
- [x] Tree model view
- [x] JSONPath & JMESPath query with autocomplete suggestions
- [x] Convert to YAML
- [x] Infer JSON Schema
- [x] Minify JSON
- [x] Custom indentation size and key sorting
- [x] Live statistics bar (keys, depth, size)
- [x] Side-by-side JSON diff checker
- [x] Generate dummy/fake JSON data
- [x] AI-powered JSON repair
- [x] Dart code generation
- [x] Kotlin code generation

## Installation

- Using IDE built-in plugin system:
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Json Parser"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:
  Download the [latest release](https://github.com/godwinjk/Json_Parser/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/godwinj)

❤️❤️❤️❤️❤️

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S0176OVQ)

## Functions example

<img src="assets/pretty.gif" width="400">
<img src="assets/input.gif" width="400">
<img src="assets/repair.gif" width="400">
<img src="assets/tree.gif" width="400">
<img src="assets/yaml.gif" width="400">
<img src="assets/query.gif" width="400">
<img src="assets/minify.gif" width="400">
<img src="assets/schema.gif" width="400">
<img src="assets/softwrap.gif" width="400">
<img src="assets/diff.gif" width="400">
<img src="assets/dummy.gif" width="400">
<img src="assets/add_tabs.gif" width="400">
<img src="assets/close_tab.gif" width="400">
<img src="assets/load_file.gif" width="400">
<img src="assets/load_url.gif" width="400">
<img src="assets/json_fn.gif" width="400">
<img src="assets/json_tabs.gif" width="400">
<img src="assets/retreive_web.gif" width="400">
<img src="assets/parse_from_anywhere.gif" width="400">
<img src="assets/load_from_file.gif" width="400">
<img src="assets/json_gen_dart.gif" width="400">
<img src="assets/kotlin_gen.gif" width="400">
<img src="assets/dart_settings.gif" width="400">
<img src="assets/kotlin_settings.gif" width="400">

## Support My Work

If you've found this project helpful or valuable, consider supporting its development!
Your contributions help me dedicate more time to improving the project, adding features, and maintaining it for the
community.
Every donation, no matter the size, makes a difference and is deeply appreciated. ❤️

## Changelog

### Version 2.0.0 🚀

A massive update packed with new tools for power users.

**Query**
- JSONPath and JMESPath query engine built right into the parser
- Live autocomplete — suggestions come from your actual JSON keys as you type
- Inline help popup with syntax reference and examples for both query languages

**Views**
- YAML tab — convert any JSON to YAML instantly with syntax highlighting
- Schema tab — infer a JSON Schema from your JSON structure automatically
- Minify tab — collapse JSON to a single line
- Statistics bar — key count, nesting depth, object/array counts, and file size at a glance

**HTTP Client**
- Full HTTP client dialog replacing the basic URL input
- Supports GET/POST with custom headers table and request body
- Response loads into a new tab automatically

**JSON Diff**
- Side-by-side editable diff viewer using IntelliJ's native diff engine
- Inline line-level highlighting — same as git merge view
- Auto-formats pasted JSON on the right side

**Dummy JSON Generator**
- Generate realistic fake JSON for testing — no external library required
- Choose Object or Array, set property count (1–100), nesting depth (1–10), and array size

**Tab Persistence**
- Sessions saved and restored across IDE restarts (up to 10 tabs)
- Tab content auto-saved on every parse

**Tree Search**
- Search bar above the tree view — filters and highlights matching nodes
- Shows JSONPath of the selected node in a label below the tree

**Onboarding Tutorial**
- Interactive tutorial with GIFs for every feature
- Auto-shows on first install, accessible anytime from the sidebar

**Quality of Life**
- Auto-parse — results update as you type, no need to press Parse
- Load from file or URL opens a new tab automatically
- Custom indentation size (1–8 spaces) and alphabetical key sorting
- Sort keys now correctly sorts nested objects recursively
- Settings moved to sidebar for quick access
- Dart freezed code generation fixed — correct `_$ClassName` mixin and `fromJson` generation
- `JsonPersistence` boolean setters fixed — settings now persist correctly across restarts
- File chooser opens at project root instead of home directory
- Toolbar creation warning resolved by setting `targetComponent`

### Version 1.9.4

1. Adding repair using AI(You can disable it in settings)

### Version 1.9.3

1. Improved the Analytics by adding session
2. Improved dart code generation
3. Tracking JSON when repair failed(This will be removed in next versions)

### Version 1.9.2

1. Improved the Analytics
2. Bug fixes
3. Added the code generation button on main parser

### Version 1.9.1

1. Implemented Analytics

### Version 1.9.0

1. Official Farewell to Java
2. New Repair function added for malformed JSON

### Version 1.8.5

Support for version 253

### Version 1.8.3

1. Bug Fixes
2. Code generation for dart issues fixed

### Version 1.8.2

1. Automation added

### Version 1.8.1

1. Added open menu for Json files in "Open In" menu
2. Changed generate menu item to last
3. Minor bug fixes`

### Version 1.8.0 🎉

You wll love this.

Json parser now support code generation. Yes you heard it right. Json Parser now support
code generation from Kotlin and Dart.

### Version 1.7.4

Added a bunch of features to JSON parser. Now you can retrieve JSON from web and also you can load from your local
system.

1. Retrieve from Web. You can add header too in the next line separated with colon symbol. Each header key value pair
   should be in the next line.
2. Load from your local system

### Version 1.7.3

1. Compatibility issue resolved

### Version 1.7.2

1. Unquoted fields and single quoted fields now supported
2. YAML comments and normal comments will not throw error anymore
3. Deprecation fixes
4. Runtime error fixed

### Version 1.7.1

1. Tree view bug fixes

### Version 1.7

1. Significant Change in tree view.
2. Major bug fixes

### Version 1.6.1

1. Updated for new latest version support

### Version 1.5

1. Now you can directly open JsonParser window from logcat or console window. No need to copy and paste from multiple
   window.
2. Tree structure updated with child number and object number for arrays.
3. Copy to clipboard added.

### Version 1.4

1. Multiple tabs added
2. Rate/ donate tab issue fixed
3. Menu item added under Edit menu (ctrl shift alt J)

### Version 1.3

1. Bug fixes

### Version 1.2

Added 3 options

- Pretty print
- Raw
- Tree

### Version 1.2.1

1. Error message if not a valid json
2. Bug fixes

### Version 1.1

Support for all platforms.

### Version 1.0

Parse VALID JSON string only.

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/godwinj)

❤️❤️❤️❤️❤️

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S0176OVQ)

## Features

- [x] Dart code generation
- [x] Kotlin code generation
- [x] Retrieve Json from web
- [x] Load Json from local
- [x] Pretty print Json
- [x] Validate Json
- [x] Single click formatting from logcat or console window
- [x] Open Json Files in tool window
- [x] Tree model view, so user can easily hide or expand particular object

## Installation

- Using IDE built-in plugin system:
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Json Parser"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:
  Download the [latest release](https://github.com/godwinjk/Json_Parser/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/godwinj)

❤️❤️❤️❤️❤️

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S0176OVQ)

## Functions example

<img src="assets/json_fn.gif" width="400">
<img src="assets/json_tabs.gif" width="400">
<img src="assets/retreive_web.gif" width="400">
<img src="assets/parse_from_anywhere.gif" width="400">
<img src="assets/load_from_file.gif" width="400">
<img src="assets/json_gen_dart.gif" width="400">
<img src="assets/kotlin_gen.gif" width="400">
<img src="assets/dart_settings.gif" width="400">
<img src="assets/kotlin_settings.gif" width="400">

## Support My Work

If you’ve found this project helpful or valuable, consider supporting its development!
Your contributions help me dedicate more time to improving the project, adding features, and maintaining it for the
community.
Every donation, no matter the size, makes a difference and is deeply appreciated. ❤️

## Changelog

### Version 1.9.4

1. Adding repair using AI(You can disable it in settings)

### Version 1.9.3

1. Improved the Analytics by adding session
2. Improved dart code generation
3. Tracking JSON when repair failed(This will be removed in next versions)

### Version 1.9.2

1. Improved the Analytics
2. Bug fixes
3. Added the code generation button on main parser

### Version 1.9.1

1. Implemented Analytics

### Version 1.9.0

1. Official Farewell to Java
2. New Repair function added for malformed JSON

### Version 1.8.5

Support for version 253

### Version 1.8.3

1. Bug Fixes
2. Code generation for dart issues fixed

### Version 1.8.2

1. Automation added

### Version 1.8.1

1. Added open menu for Json files in "Open In" menu
2. Changed generate menu item to last
3. Minor bug fixes`

### Version 1.8.0 🎉

You wll love this.

Json parser now support code generation. Yes you heard it right. Json Parser now support
code generation from Kotlin and Dart.

### Version 1.7.4

Added a bunch of features to JSON parser. Now you can retrieve JSON from web and also you can load from your local
system.

1. Retrieve from Web. You can add header too in the next line separated with colon symbol. Each header key value pair
   should be in the next line.
2. Load from your local system

### Version 1.7.3

1. Compatibility issue resolved

### Version 1.7.2

1. Unquoted fields and single quoted fields now supported
2. YAML comments and normal comments will not throw error anymore
3. Deprecation fixes
4. Runtime error fixed

### Version 1.7.1

1. Tree view bug fixes

### Version 1.7

1. Significant Change in tree view.
2. Major bug fixes

### Version 1.6.1

1. Updated for new latest version support

### Version 1.5

1. Now you can directly open JsonParser window from logcat or console window. No need to copy and paste from multiple
   window.
2. Tree structure updated with child number and object number for arrays.
3. Copy to clipboard added.

### Version 1.4

1. Multiple tabs added
2. Rate/ donate tab issue fixed
3. Menu item added under Edit menu (ctrl shift alt J)

### Version 1.3

1. Bug fixes

### Version 1.2

Added 3 options

- Pretty print
- Raw
- Tree

### Version 1.2.1

1. Error message if not a valid json
2. Bug fixes

### Version 1.1

Support for all platforms.

### Version 1.0

Parse VALID JSON string only.

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/godwinj)

❤️❤️❤️❤️❤️

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S0176OVQ)