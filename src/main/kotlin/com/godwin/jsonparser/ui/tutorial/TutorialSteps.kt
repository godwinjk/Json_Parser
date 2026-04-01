package com.godwin.jsonparser.ui.tutorial

import javax.swing.JComponent

object TutorialSteps {

    var parseButton: JComponent? = null
    var repairButton: JComponent? = null
    var inputEditor: JComponent? = null
    var toolbarComponent: JComponent? = null

    private fun gif(name: String) = "/onboarding/$name"

    fun all(): List<TutorialStep> = listOf(
        TutorialStep(
            id = "welcome",
            title = "Welcome to JSON Parser 👋",
            description = "This quick tour shows you everything the plugin can do. Use Next/Prev to navigate or Skip to dismiss at any time.",
            gifResource = gif("pretty.gif")
        ), TutorialStep(
            id = "input",
            title = "Input Editor",
            description = "Paste or type your JSON here. The parser auto-formats as you type — no need to press Parse manually.",
            gifResource = gif("input.gif"),
            targetComponent = inputEditor,
        ), TutorialStep(
            id = "parse",
            title = "Parse & Pretty Print",
            description = "Click Parse to format and validate your JSON. Results appear instantly with syntax highlighting.",
            gifResource = gif("pretty.gif"),
            targetComponent = parseButton,
        ), TutorialStep(
            id = "repair",
            title = "Repair Malformed JSON",
            description = "When your JSON has errors, the Repair button appears. It uses a local engine and optionally AI to fix issues automatically.",
            gifResource = gif("repair.gif"),
            targetComponent = repairButton,
        ), TutorialStep(
            id = "tree",
            title = "Tree View",
            description = "Expand and collapse JSON nodes. Use the search bar to find keys or values — it shows the JSONPath of the selected node.",
            gifResource = gif("tree.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "yaml",
            title = "YAML Conversion",
            description = "Converts your JSON to YAML automatically with full syntax highlighting.",
            gifResource = gif("yaml.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "query",
            title = "JSONPath & JMESPath Query",
            description = "Run JSONPath or JMESPath expressions. Autocomplete suggests paths from your actual JSON keys as you type.",
            gifResource = gif("query.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "minify",
            title = "Minify",
            description = "Collapses your JSON to a single line — useful for copying into API requests.",
            gifResource = gif("minify.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "schema",
            title = "JSON Schema",
            description = "Infers a JSON Schema from your data structure automatically.",
            gifResource = gif("schema.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "softwrap",
            title = "Soft Wrap",
            description = "Toggle soft wraps so long lines wrap instead of scrolling horizontally.",
            gifResource = gif("softwrap.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "add_tab",
            title = "Multiple Tabs",
            description = "Click + to open a new parser session. Each tab is independent and sessions are saved across IDE restarts.",
            gifResource = gif("add_tabs.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "close_tab",
            title = "Close a Tab",
            description = "Click the × on a tab to close it, or use the close button in the sidebar.",
            gifResource = gif("close_tab.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "load_file",
            title = "Load from File",
            description = "Click the folder icon to load a JSON file from disk. It opens in a new tab automatically.",
            gifResource = gif("load_file.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "load_url",
            title = "HTTP Client",
            description = "Click the download icon to open the HTTP client. Supports GET/POST with custom headers.",
            gifResource = gif("load_url.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "generate",
            title = "Generate Dummy JSON",
            description = "Create realistic fake JSON for testing. Choose object/array, nesting depth, and array size.",
            gifResource = gif("dummy.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "diff",
            title = "JSON Diff",
            description = "Side-by-side diff viewer with inline line-level highlighting. Paste any JSON on the right to compare.",
            gifResource = gif("diff.gif"),
            targetComponent = toolbarComponent,
        ), TutorialStep(
            id = "done",
            title = "You're all set! 🎉",
            description = "You now know everything JSON Parser can do. Start by pasting your JSON and exploring the tabs. Happy parsing!",
            gifResource = gif("pretty.gif")
        )
    )
}
