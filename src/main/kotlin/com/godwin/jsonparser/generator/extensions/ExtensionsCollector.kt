package com.godwin.jsonparser.generator.extensions


/**
 * extension collect, all extensions will be hold by this class's extensions property
 */
object ExtensionsCollector {
    /**
     * all extensions
     */
    val extensions = listOf(
        PropertyPrefixSupport,
        PropertySuffixSupport,
        ClassNameSuffixSupport
    )
}
