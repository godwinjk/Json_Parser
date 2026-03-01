package com.godwin.jsonparser.common.exception


/**
 * Throw out when the json to be convert don't support by this plugin or no need to convert to any classes
 */
class InputTooLargeException(message: String = "") : Exception(message)