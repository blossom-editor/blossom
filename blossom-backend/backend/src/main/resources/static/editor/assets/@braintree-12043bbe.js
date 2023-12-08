var commonjsGlobal = typeof globalThis !== "undefined" ? globalThis : typeof window !== "undefined" ? window : typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : {};
function getDefaultExportFromCjs(x) {
  return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, "default") ? x["default"] : x;
}
function getAugmentedNamespace(n) {
  if (n.__esModule)
    return n;
  var f = n.default;
  if (typeof f == "function") {
    var a = function a2() {
      if (this instanceof a2) {
        return Reflect.construct(f, arguments, this.constructor);
      }
      return f.apply(this, arguments);
    };
    a.prototype = f.prototype;
  } else
    a = {};
  Object.defineProperty(a, "__esModule", { value: true });
  Object.keys(n).forEach(function(k) {
    var d = Object.getOwnPropertyDescriptor(n, k);
    Object.defineProperty(a, k, d.get ? d : {
      enumerable: true,
      get: function() {
        return n[k];
      }
    });
  });
  return a;
}
var dist = {};
(function(exports) {
  Object.defineProperty(exports, "__esModule", { value: true });
  exports.sanitizeUrl = exports.BLANK_URL = void 0;
  var invalidProtocolRegex = /^([^\w]*)(javascript|data|vbscript)/im;
  var htmlEntitiesRegex = /&#(\w+)(^\w|;)?/g;
  var htmlCtrlEntityRegex = /&(newline|tab);/gi;
  var ctrlCharactersRegex = /[\u0000-\u001F\u007F-\u009F\u2000-\u200D\uFEFF]/gim;
  var urlSchemeRegex = /^.+(:|&colon;)/gim;
  var relativeFirstCharacters = [".", "/"];
  exports.BLANK_URL = "about:blank";
  function isRelativeUrlWithoutProtocol(url) {
    return relativeFirstCharacters.indexOf(url[0]) > -1;
  }
  function decodeHtmlCharacters(str) {
    var removedNullByte = str.replace(ctrlCharactersRegex, "");
    return removedNullByte.replace(htmlEntitiesRegex, function(match, dec) {
      return String.fromCharCode(dec);
    });
  }
  function sanitizeUrl(url) {
    if (!url) {
      return exports.BLANK_URL;
    }
    var sanitizedUrl = decodeHtmlCharacters(url).replace(htmlCtrlEntityRegex, "").replace(ctrlCharactersRegex, "").trim();
    if (!sanitizedUrl) {
      return exports.BLANK_URL;
    }
    if (isRelativeUrlWithoutProtocol(sanitizedUrl)) {
      return sanitizedUrl;
    }
    var urlSchemeParseResults = sanitizedUrl.match(urlSchemeRegex);
    if (!urlSchemeParseResults) {
      return sanitizedUrl;
    }
    var urlScheme = urlSchemeParseResults[0];
    if (invalidProtocolRegex.test(urlScheme)) {
      return exports.BLANK_URL;
    }
    return sanitizedUrl;
  }
  exports.sanitizeUrl = sanitizeUrl;
})(dist);
export {
  getAugmentedNamespace as a,
  commonjsGlobal as c,
  dist as d,
  getDefaultExportFromCjs as g
};
