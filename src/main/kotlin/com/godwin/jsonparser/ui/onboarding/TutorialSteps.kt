package com.godwin.jsonparser.ui.onboarding

import javax.swing.JComponent

/**
 * Central registry of all tutorial steps.
 * Components are injected at runtime when the tutorial is launched.
 */
object TutorialSteps {

    // Component references — set by ParserWidget / ProjectService before starting
    var addTabButton: JComponent? = null
    var closeTabButton: JComponent? = null
    var loadFileButton: JComponent? = null
    var loadUrlButton: JComponent? = null
    var generateDummyButton: JComponent? = null
    var diffButton: JComponent? = null
    var settingsButton: JComponent? = null
    var parseButton: JComponent? = null
    var repairButton: JComponent? = null
    var prettyTabButton: JComponent? = null
    var treeTabButton: JComponent? = null
    var yamlTabButton: JComponent? = null
    var queryTabButton: JComponent? = null
    var minifyTabButton: JComponent? = null
    var schemaTabButton: JComponent? = null
    var inputEditor: JComponent? = null

    private const val GIF_PRETTY = "/onboarding/pretty.gif"
    private const val GIF_TREE = "/onboarding/tree.gif"
    private const val GIF_YAML = "/onboarding/yaml.gif"
    private const val GIF_QUERY = "/onboarding/query.gif"
    private const val GIF_MINIFY = "/onboarding/minify.gif"
    private const val GIF_SCHEMA = "/onboarding/schema.gif"
    private const val GIF_ADD_TABS = "/onboarding/add_tabs.gif"
    private const val GIF_CLOSE_TAB = "/onboarding/close_tab.gif"
    private const val GIF_LOAD_FILE = "/onboarding/load_file.gif"
    private const val GIF_LOAD_URL = "/onboarding/load_url.gif"
    private const val GIF_DUMMY = "/onboarding/dummy.gif"
    private const val GIF_DIFF = "/onboarding/diff.gif"
    private const val GIF_SOFTWRAP = "/onboarding/softwrap.gif"

    fun all(): List<TutorialStep> = listOf(
        TutorialStep(
            id = "welcome",
            title = "Welcome to JSON Parser",
            description = "This quick tour will show you all the features. Use Next/Prev to navigate or Skip to dismiss.",
            gifResource = GIF_PRETTY,
            targetComponent = null
        ),
        TutorialStep(
            id = "input",
            title = "Input Editor",
            description = "Paste or type your JSON here. The parser auto-formats as you type — no need to press Parse manually.",
            gifResource = GIF_SOFTWRAP,
            targetComponent = inputEditor,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "parse",
            title = "Parse Button",
            description = "Click Parse to manually trigger formatting and validation. Results appear instantly in the output panel below.",
            gifResource = GIF_PRETTY,
            targetComponent = parseButton,
            arrowSide = ArrowSide.TOP
        ),
        TutorialStep(
            id = "repair",
            title = "Repair Malformed JSON",
            description = "If your JSON has errors, the Repair button appears. It uses a local engine and optionally AI to fix common issues.",
            gifResource = GIF_PRETTY,
            targetComponent = repairButton,
            arrowSide = ArrowSide.TOP
        ),
        TutorialStep(
            id = "pretty",
            title = "Pretty View",
            description = "The Pretty tab shows your JSON formatted with syntax highlighting and configurable indentation.",
            gifResource = GIF_PRETTY,
            targetComponent = prettyTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "tree",
            title = "Tree View",
            description = "The Tree tab lets you expand and collapse JSON nodes. Use the search bar to find keys or values instantly.",
            gifResource = GIF_TREE,
            targetComponent = treeTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "yaml",
            title = "YAML Conversion",
            description = "The YAML tab converts your JSON to YAML automatically with full syntax highlighting.",
            gifResource = GIF_YAML,
            targetComponent = yamlTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "query",
            title = "JSONPath & JMESPath Query",
            description = "The Query tab lets you run JSONPath or JMESPath expressions. Autocomplete suggests paths from your actual JSON keys.",
            gifResource = GIF_QUERY,
            targetComponent = queryTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "minify",
            title = "Minify",
            description = "The Minify tab collapses your JSON to a single line — useful for copying into API requests.",
            gifResource = GIF_MINIFY,
            targetComponent = minifyTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "schema",
            title = "JSON Schema",
            description = "The Schema tab infers a JSON Schema from your data structure automatically.",
            gifResource = GIF_SCHEMA,
            targetComponent = schemaTabButton,
            arrowSide = ArrowSide.BOTTOM
        ),
        TutorialStep(
            id = "add_tab",
            title = "Multiple Tabs",
            description = "Click + to open a new parser session. Each tab is independent — your sessions are saved and restored on restart.",
            gifResource = GIF_ADD_TABS,
            targetComponent = addTabButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "close_tab",
            title = "Close Tab",
            description = "Click the close button on each tab to close that session individually.",
            gifResource = GIF_CLOSE_TAB,
            targetComponent = closeTabButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "load_file",
            title = "Load from File",
            description = "Click the folder icon to load a JSON file from your disk. It opens in a new tab automatically.",
            gifResource = GIF_LOAD_FILE,
            targetComponent = loadFileButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "load_url",
            title = "Load from URL",
            description = "Click the download icon to open the HTTP client. Supports GET/POST with custom headers.",
            gifResource = GIF_LOAD_URL,
            targetComponent = loadUrlButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "generate",
            title = "Generate Dummy JSON",
            description = "Click the generate icon to create realistic fake JSON for testing. Choose object/array, depth, and size.",
            gifResource = GIF_DUMMY,
            targetComponent = generateDummyButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "diff",
            title = "JSON Diff",
            description = "Click the diff icon to open a side-by-side diff viewer. Paste any JSON on the right to see inline differences.",
            gifResource = GIF_DIFF,
            targetComponent = diffButton,
            arrowSide = ArrowSide.RIGHT
        ),
        TutorialStep(
            id = "settings",
            title = "Parser Settings",
            description = "Click the gear icon to configure indentation size (1–8 spaces) and alphabetical key sorting.",
            gifResource = GIF_SOFTWRAP,
            targetComponent = settingsButton,
            arrowSide = ArrowSide.RIGHT
        )
    )
}
