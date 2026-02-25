package com.godwin.jsonparser.generator.jsontokotlin.extensions

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.AnalyticsSwitchSupport

/**
 * extension collect, all extensions will be hold by this class's extensions property
 */
object ExtensionsCollector {
    /**
     * all extensions
     */
    val extensions = listOf(
        com.godwin.jsonparser.generator.jsontokotlin.extensions.chen.biao.KeepAnnotationSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.KeepAnnotationSupportForAndroidX,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.ted.zeng.PropertyAnnotationLineSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.jose.han.ParcelableAnnotationSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.PropertyPrefixSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.PropertySuffixSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.ClassNamePrefixSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.ClassNameSuffixSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.xu.rui.PrimitiveTypeNonNullableSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.ForceInitDefaultValueWithOriginJsonValueSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.DisableDataClassSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.nstd.ReplaceConstructorParametersByMemberVariablesSupport,
        AnalyticsSwitchSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.yuan.varenyzc.CamelCaseSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.yuan.varenyzc.BuildFromJsonObjectSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.yuan.varenyzc.NeedNonNullableClassesSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.InternalModifierSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.AddGsonExposeAnnotationSupport,
        com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal.BaseClassSupport
    )
}
