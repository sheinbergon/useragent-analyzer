var UAParserWrapper = function(uastring) {
    return JSON.stringify(UAParser(uastring));
}