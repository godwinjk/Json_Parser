package com.godwin.jsonparser.generator_kt.extensions

import com.godwin.jsonparser.generator_kt.extensions.chen.biao.KeepAnnotationSupport
import com.godwin.jsonparser.generator_kt.extensions.jose.han.ParcelableAnnotationSupport
import com.godwin.jsonparser.generator_kt.extensions.ted.zeng.PropertyAnnotationLineSupport
import com.godwin.jsonparser.generator_kt.extensions.xu.rui.PrimitiveTypeNonNullableSupport
import com.godwin.jsonparser.generator_kt.extensions.nstd.ReplaceConstructorParametersByMemberVariablesSupport
import com.godwin.jsonparser.generator_kt.extensions.wu.seal.*
import com.godwin.jsonparser.generator_kt.extensions.yuan.varenyzc.BuildFromJsonObjectSupport
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.AnalyticsSwitchSupport
import com.godwin.jsonparser.generator_kt.extensions.yuan.varenyzc.CamelCaseSupport
import com.godwin.jsonparser.generator_kt.extensions.yuan.varenyzc.NeedNonNullableClassesSupport

/**
 * extension collect, all extensions will be hold by this class's extensions property
 */
object ExtensionsCollector {
    /**
     * all extensions
     */
    val extensions = listOf(
        KeepAnnotationSupport,
        KeepAnnotationSupportForAndroidX,
        PropertyAnnotationLineSupport,
        ParcelableAnnotationSupport,
        PropertyPrefixSupport,
        PropertySuffixSupport,
        ClassNamePrefixSupport,
        ClassNameSuffixSupport,
        PrimitiveTypeNonNullableSupport,
        ForceInitDefaultValueWithOriginJsonValueSupport,
        DisableDataClassSupport,
        ReplaceConstructorParametersByMemberVariablesSupport,
        AnalyticsSwitchSupport,
        CamelCaseSupport,
        BuildFromJsonObjectSupport,
        NeedNonNullableClassesSupport,
        InternalModifierSupport,
        AddGsonExposeAnnotationSupport,
        BaseClassSupport
    )
}
