const Channel = {
  /* CLAMP */
  min: {
    r: 0,
    g: 0,
    b: 0,
    s: 0,
    l: 0,
    a: 0
  },
  max: {
    r: 255,
    g: 255,
    b: 255,
    h: 360,
    s: 100,
    l: 100,
    a: 1
  },
  clamp: {
    r: (r) => r >= 255 ? 255 : r < 0 ? 0 : r,
    g: (g) => g >= 255 ? 255 : g < 0 ? 0 : g,
    b: (b) => b >= 255 ? 255 : b < 0 ? 0 : b,
    h: (h) => h % 360,
    s: (s) => s >= 100 ? 100 : s < 0 ? 0 : s,
    l: (l) => l >= 100 ? 100 : l < 0 ? 0 : l,
    a: (a) => a >= 1 ? 1 : a < 0 ? 0 : a
  },
  /* CONVERSION */
  //SOURCE: https://planetcalc.com/7779
  toLinear: (c) => {
    const n = c / 255;
    return c > 0.03928 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
  },
  //SOURCE: https://gist.github.com/mjackson/5311256
  hue2rgb: (p, q, t) => {
    if (t < 0)
      t += 1;
    if (t > 1)
      t -= 1;
    if (t < 1 / 6)
      return p + (q - p) * 6 * t;
    if (t < 1 / 2)
      return q;
    if (t < 2 / 3)
      return p + (q - p) * (2 / 3 - t) * 6;
    return p;
  },
  hsl2rgb: ({ h, s, l }, channel2) => {
    if (!s)
      return l * 2.55;
    h /= 360;
    s /= 100;
    l /= 100;
    const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
    const p = 2 * l - q;
    switch (channel2) {
      case "r":
        return Channel.hue2rgb(p, q, h + 1 / 3) * 255;
      case "g":
        return Channel.hue2rgb(p, q, h) * 255;
      case "b":
        return Channel.hue2rgb(p, q, h - 1 / 3) * 255;
    }
  },
  rgb2hsl: ({ r, g, b }, channel2) => {
    r /= 255;
    g /= 255;
    b /= 255;
    const max = Math.max(r, g, b);
    const min = Math.min(r, g, b);
    const l = (max + min) / 2;
    if (channel2 === "l")
      return l * 100;
    if (max === min)
      return 0;
    const d = max - min;
    const s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
    if (channel2 === "s")
      return s * 100;
    switch (max) {
      case r:
        return ((g - b) / d + (g < b ? 6 : 0)) * 60;
      case g:
        return ((b - r) / d + 2) * 60;
      case b:
        return ((r - g) / d + 4) * 60;
      default:
        return -1;
    }
  }
};
const channel$2 = Channel;
const Lang = {
  /* API */
  clamp: (number, lower, upper) => {
    if (lower > upper)
      return Math.min(lower, Math.max(upper, number));
    return Math.min(upper, Math.max(lower, number));
  },
  round: (number) => {
    return Math.round(number * 1e10) / 1e10;
  }
};
const lang = Lang;
const Unit = {
  /* API */
  dec2hex: (dec) => {
    const hex = Math.round(dec).toString(16);
    return hex.length > 1 ? hex : `0${hex}`;
  }
};
const unit = Unit;
const Utils = {
  channel: channel$2,
  lang,
  unit
};
const _ = Utils;
const DEC2HEX = {};
for (let i = 0; i <= 255; i++)
  DEC2HEX[i] = _.unit.dec2hex(i);
const TYPE = {
  ALL: 0,
  RGB: 1,
  HSL: 2
};
class Type {
  constructor() {
    this.type = TYPE.ALL;
  }
  /* API */
  get() {
    return this.type;
  }
  set(type) {
    if (this.type && this.type !== type)
      throw new Error("Cannot change both RGB and HSL channels at the same time");
    this.type = type;
  }
  reset() {
    this.type = TYPE.ALL;
  }
  is(type) {
    return this.type === type;
  }
}
const Type$1 = Type;
class Channels {
  /* CONSTRUCTOR */
  constructor(data, color) {
    this.color = color;
    this.changed = false;
    this.data = data;
    this.type = new Type$1();
  }
  /* API */
  set(data, color) {
    this.color = color;
    this.changed = false;
    this.data = data;
    this.type.type = TYPE.ALL;
    return this;
  }
  /* HELPERS */
  _ensureHSL() {
    const data = this.data;
    const { h, s, l } = data;
    if (h === void 0)
      data.h = _.channel.rgb2hsl(data, "h");
    if (s === void 0)
      data.s = _.channel.rgb2hsl(data, "s");
    if (l === void 0)
      data.l = _.channel.rgb2hsl(data, "l");
  }
  _ensureRGB() {
    const data = this.data;
    const { r, g, b } = data;
    if (r === void 0)
      data.r = _.channel.hsl2rgb(data, "r");
    if (g === void 0)
      data.g = _.channel.hsl2rgb(data, "g");
    if (b === void 0)
      data.b = _.channel.hsl2rgb(data, "b");
  }
  /* GETTERS */
  get r() {
    const data = this.data;
    const r = data.r;
    if (!this.type.is(TYPE.HSL) && r !== void 0)
      return r;
    this._ensureHSL();
    return _.channel.hsl2rgb(data, "r");
  }
  get g() {
    const data = this.data;
    const g = data.g;
    if (!this.type.is(TYPE.HSL) && g !== void 0)
      return g;
    this._ensureHSL();
    return _.channel.hsl2rgb(data, "g");
  }
  get b() {
    const data = this.data;
    const b = data.b;
    if (!this.type.is(TYPE.HSL) && b !== void 0)
      return b;
    this._ensureHSL();
    return _.channel.hsl2rgb(data, "b");
  }
  get h() {
    const data = this.data;
    const h = data.h;
    if (!this.type.is(TYPE.RGB) && h !== void 0)
      return h;
    this._ensureRGB();
    return _.channel.rgb2hsl(data, "h");
  }
  get s() {
    const data = this.data;
    const s = data.s;
    if (!this.type.is(TYPE.RGB) && s !== void 0)
      return s;
    this._ensureRGB();
    return _.channel.rgb2hsl(data, "s");
  }
  get l() {
    const data = this.data;
    const l = data.l;
    if (!this.type.is(TYPE.RGB) && l !== void 0)
      return l;
    this._ensureRGB();
    return _.channel.rgb2hsl(data, "l");
  }
  get a() {
    return this.data.a;
  }
  /* SETTERS */
  set r(r) {
    this.type.set(TYPE.RGB);
    this.changed = true;
    this.data.r = r;
  }
  set g(g) {
    this.type.set(TYPE.RGB);
    this.changed = true;
    this.data.g = g;
  }
  set b(b) {
    this.type.set(TYPE.RGB);
    this.changed = true;
    this.data.b = b;
  }
  set h(h) {
    this.type.set(TYPE.HSL);
    this.changed = true;
    this.data.h = h;
  }
  set s(s) {
    this.type.set(TYPE.HSL);
    this.changed = true;
    this.data.s = s;
  }
  set l(l) {
    this.type.set(TYPE.HSL);
    this.changed = true;
    this.data.l = l;
  }
  set a(a) {
    this.changed = true;
    this.data.a = a;
  }
}
const Channels$1 = Channels;
const channels = new Channels$1({ r: 0, g: 0, b: 0, a: 0 }, "transparent");
const ChannelsReusable = channels;
const Hex = {
  /* VARIABLES */
  re: /^#((?:[a-f0-9]{2}){2,4}|[a-f0-9]{3})$/i,
  /* API */
  parse: (color) => {
    if (color.charCodeAt(0) !== 35)
      return;
    const match = color.match(Hex.re);
    if (!match)
      return;
    const hex = match[1];
    const dec = parseInt(hex, 16);
    const length = hex.length;
    const hasAlpha = length % 4 === 0;
    const isFullLength = length > 4;
    const multiplier = isFullLength ? 1 : 17;
    const bits = isFullLength ? 8 : 4;
    const bitsOffset = hasAlpha ? 0 : -1;
    const mask = isFullLength ? 255 : 15;
    return ChannelsReusable.set({
      r: (dec >> bits * (bitsOffset + 3) & mask) * multiplier,
      g: (dec >> bits * (bitsOffset + 2) & mask) * multiplier,
      b: (dec >> bits * (bitsOffset + 1) & mask) * multiplier,
      a: hasAlpha ? (dec & mask) * multiplier / 255 : 1
    }, color);
  },
  stringify: (channels2) => {
    const { r, g, b, a } = channels2;
    if (a < 1) {
      return `#${DEC2HEX[Math.round(r)]}${DEC2HEX[Math.round(g)]}${DEC2HEX[Math.round(b)]}${DEC2HEX[Math.round(a * 255)]}`;
    } else {
      return `#${DEC2HEX[Math.round(r)]}${DEC2HEX[Math.round(g)]}${DEC2HEX[Math.round(b)]}`;
    }
  }
};
const Hex$1 = Hex;
const HSL = {
  /* VARIABLES */
  re: /^hsla?\(\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e-?\d+)?(?:deg|grad|rad|turn)?)\s*?(?:,|\s)\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e-?\d+)?%)\s*?(?:,|\s)\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e-?\d+)?%)(?:\s*?(?:,|\/)\s*?\+?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e-?\d+)?(%)?))?\s*?\)$/i,
  hueRe: /^(.+?)(deg|grad|rad|turn)$/i,
  /* HELPERS */
  _hue2deg: (hue) => {
    const match = hue.match(HSL.hueRe);
    if (match) {
      const [, number, unit2] = match;
      switch (unit2) {
        case "grad":
          return _.channel.clamp.h(parseFloat(number) * 0.9);
        case "rad":
          return _.channel.clamp.h(parseFloat(number) * 180 / Math.PI);
        case "turn":
          return _.channel.clamp.h(parseFloat(number) * 360);
      }
    }
    return _.channel.clamp.h(parseFloat(hue));
  },
  /* API */
  parse: (color) => {
    const charCode = color.charCodeAt(0);
    if (charCode !== 104 && charCode !== 72)
      return;
    const match = color.match(HSL.re);
    if (!match)
      return;
    const [, h, s, l, a, isAlphaPercentage] = match;
    return ChannelsReusable.set({
      h: HSL._hue2deg(h),
      s: _.channel.clamp.s(parseFloat(s)),
      l: _.channel.clamp.l(parseFloat(l)),
      a: a ? _.channel.clamp.a(isAlphaPercentage ? parseFloat(a) / 100 : parseFloat(a)) : 1
    }, color);
  },
  stringify: (channels2) => {
    const { h, s, l, a } = channels2;
    if (a < 1) {
      return `hsla(${_.lang.round(h)}, ${_.lang.round(s)}%, ${_.lang.round(l)}%, ${a})`;
    } else {
      return `hsl(${_.lang.round(h)}, ${_.lang.round(s)}%, ${_.lang.round(l)}%)`;
    }
  }
};
const HSL$1 = HSL;
const Keyword = {
  /* VARIABLES */
  colors: {
    aliceblue: "#f0f8ff",
    antiquewhite: "#faebd7",
    aqua: "#00ffff",
    aquamarine: "#7fffd4",
    azure: "#f0ffff",
    beige: "#f5f5dc",
    bisque: "#ffe4c4",
    black: "#000000",
    blanchedalmond: "#ffebcd",
    blue: "#0000ff",
    blueviolet: "#8a2be2",
    brown: "#a52a2a",
    burlywood: "#deb887",
    cadetblue: "#5f9ea0",
    chartreuse: "#7fff00",
    chocolate: "#d2691e",
    coral: "#ff7f50",
    cornflowerblue: "#6495ed",
    cornsilk: "#fff8dc",
    crimson: "#dc143c",
    cyanaqua: "#00ffff",
    darkblue: "#00008b",
    darkcyan: "#008b8b",
    darkgoldenrod: "#b8860b",
    darkgray: "#a9a9a9",
    darkgreen: "#006400",
    darkgrey: "#a9a9a9",
    darkkhaki: "#bdb76b",
    darkmagenta: "#8b008b",
    darkolivegreen: "#556b2f",
    darkorange: "#ff8c00",
    darkorchid: "#9932cc",
    darkred: "#8b0000",
    darksalmon: "#e9967a",
    darkseagreen: "#8fbc8f",
    darkslateblue: "#483d8b",
    darkslategray: "#2f4f4f",
    darkslategrey: "#2f4f4f",
    darkturquoise: "#00ced1",
    darkviolet: "#9400d3",
    deeppink: "#ff1493",
    deepskyblue: "#00bfff",
    dimgray: "#696969",
    dimgrey: "#696969",
    dodgerblue: "#1e90ff",
    firebrick: "#b22222",
    floralwhite: "#fffaf0",
    forestgreen: "#228b22",
    fuchsia: "#ff00ff",
    gainsboro: "#dcdcdc",
    ghostwhite: "#f8f8ff",
    gold: "#ffd700",
    goldenrod: "#daa520",
    gray: "#808080",
    green: "#008000",
    greenyellow: "#adff2f",
    grey: "#808080",
    honeydew: "#f0fff0",
    hotpink: "#ff69b4",
    indianred: "#cd5c5c",
    indigo: "#4b0082",
    ivory: "#fffff0",
    khaki: "#f0e68c",
    lavender: "#e6e6fa",
    lavenderblush: "#fff0f5",
    lawngreen: "#7cfc00",
    lemonchiffon: "#fffacd",
    lightblue: "#add8e6",
    lightcoral: "#f08080",
    lightcyan: "#e0ffff",
    lightgoldenrodyellow: "#fafad2",
    lightgray: "#d3d3d3",
    lightgreen: "#90ee90",
    lightgrey: "#d3d3d3",
    lightpink: "#ffb6c1",
    lightsalmon: "#ffa07a",
    lightseagreen: "#20b2aa",
    lightskyblue: "#87cefa",
    lightslategray: "#778899",
    lightslategrey: "#778899",
    lightsteelblue: "#b0c4de",
    lightyellow: "#ffffe0",
    lime: "#00ff00",
    limegreen: "#32cd32",
    linen: "#faf0e6",
    magenta: "#ff00ff",
    maroon: "#800000",
    mediumaquamarine: "#66cdaa",
    mediumblue: "#0000cd",
    mediumorchid: "#ba55d3",
    mediumpurple: "#9370db",
    mediumseagreen: "#3cb371",
    mediumslateblue: "#7b68ee",
    mediumspringgreen: "#00fa9a",
    mediumturquoise: "#48d1cc",
    mediumvioletred: "#c71585",
    midnightblue: "#191970",
    mintcream: "#f5fffa",
    mistyrose: "#ffe4e1",
    moccasin: "#ffe4b5",
    navajowhite: "#ffdead",
    navy: "#000080",
    oldlace: "#fdf5e6",
    olive: "#808000",
    olivedrab: "#6b8e23",
    orange: "#ffa500",
    orangered: "#ff4500",
    orchid: "#da70d6",
    palegoldenrod: "#eee8aa",
    palegreen: "#98fb98",
    paleturquoise: "#afeeee",
    palevioletred: "#db7093",
    papayawhip: "#ffefd5",
    peachpuff: "#ffdab9",
    peru: "#cd853f",
    pink: "#ffc0cb",
    plum: "#dda0dd",
    powderblue: "#b0e0e6",
    purple: "#800080",
    rebeccapurple: "#663399",
    red: "#ff0000",
    rosybrown: "#bc8f8f",
    royalblue: "#4169e1",
    saddlebrown: "#8b4513",
    salmon: "#fa8072",
    sandybrown: "#f4a460",
    seagreen: "#2e8b57",
    seashell: "#fff5ee",
    sienna: "#a0522d",
    silver: "#c0c0c0",
    skyblue: "#87ceeb",
    slateblue: "#6a5acd",
    slategray: "#708090",
    slategrey: "#708090",
    snow: "#fffafa",
    springgreen: "#00ff7f",
    tan: "#d2b48c",
    teal: "#008080",
    thistle: "#d8bfd8",
    transparent: "#00000000",
    turquoise: "#40e0d0",
    violet: "#ee82ee",
    wheat: "#f5deb3",
    white: "#ffffff",
    whitesmoke: "#f5f5f5",
    yellow: "#ffff00",
    yellowgreen: "#9acd32"
  },
  /* API */
  parse: (color) => {
    color = color.toLowerCase();
    const hex = Keyword.colors[color];
    if (!hex)
      return;
    return Hex$1.parse(hex);
  },
  stringify: (channels2) => {
    const hex = Hex$1.stringify(channels2);
    for (const name in Keyword.colors) {
      if (Keyword.colors[name] === hex)
        return name;
    }
    return;
  }
};
const Keyword$1 = Keyword;
const RGB = {
  /* VARIABLES */
  re: /^rgba?\(\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e\d+)?(%?))\s*?(?:,|\s)\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e\d+)?(%?))\s*?(?:,|\s)\s*?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e\d+)?(%?))(?:\s*?(?:,|\/)\s*?\+?(-?(?:\d+(?:\.\d+)?|(?:\.\d+))(?:e\d+)?(%?)))?\s*?\)$/i,
  /* API */
  parse: (color) => {
    const charCode = color.charCodeAt(0);
    if (charCode !== 114 && charCode !== 82)
      return;
    const match = color.match(RGB.re);
    if (!match)
      return;
    const [, r, isRedPercentage, g, isGreenPercentage, b, isBluePercentage, a, isAlphaPercentage] = match;
    return ChannelsReusable.set({
      r: _.channel.clamp.r(isRedPercentage ? parseFloat(r) * 2.55 : parseFloat(r)),
      g: _.channel.clamp.g(isGreenPercentage ? parseFloat(g) * 2.55 : parseFloat(g)),
      b: _.channel.clamp.b(isBluePercentage ? parseFloat(b) * 2.55 : parseFloat(b)),
      a: a ? _.channel.clamp.a(isAlphaPercentage ? parseFloat(a) / 100 : parseFloat(a)) : 1
    }, color);
  },
  stringify: (channels2) => {
    const { r, g, b, a } = channels2;
    if (a < 1) {
      return `rgba(${_.lang.round(r)}, ${_.lang.round(g)}, ${_.lang.round(b)}, ${_.lang.round(a)})`;
    } else {
      return `rgb(${_.lang.round(r)}, ${_.lang.round(g)}, ${_.lang.round(b)})`;
    }
  }
};
const RGB$1 = RGB;
const Color = {
  /* VARIABLES */
  format: {
    keyword: Keyword$1,
    hex: Hex$1,
    rgb: RGB$1,
    rgba: RGB$1,
    hsl: HSL$1,
    hsla: HSL$1
  },
  /* API */
  parse: (color) => {
    if (typeof color !== "string")
      return color;
    const channels2 = Hex$1.parse(color) || RGB$1.parse(color) || HSL$1.parse(color) || Keyword$1.parse(color);
    if (channels2)
      return channels2;
    throw new Error(`Unsupported color format: "${color}"`);
  },
  stringify: (channels2) => {
    if (!channels2.changed && channels2.color)
      return channels2.color;
    if (channels2.type.is(TYPE.HSL) || channels2.data.r === void 0) {
      return HSL$1.stringify(channels2);
    } else if (channels2.a < 1 || !Number.isInteger(channels2.r) || !Number.isInteger(channels2.g) || !Number.isInteger(channels2.b)) {
      return RGB$1.stringify(channels2);
    } else {
      return Hex$1.stringify(channels2);
    }
  }
};
const Color$1 = Color;
const change = (color, channels2) => {
  const ch = Color$1.parse(color);
  for (const c in channels2) {
    ch[c] = _.channel.clamp[c](channels2[c]);
  }
  return Color$1.stringify(ch);
};
const change$1 = change;
const rgba = (r, g, b = 0, a = 1) => {
  if (typeof r !== "number")
    return change$1(r, { a: g });
  const channels2 = ChannelsReusable.set({
    r: _.channel.clamp.r(r),
    g: _.channel.clamp.g(g),
    b: _.channel.clamp.b(b),
    a: _.channel.clamp.a(a)
  });
  return Color$1.stringify(channels2);
};
const rgba$1 = rgba;
const channel = (color, channel2) => {
  return _.lang.round(Color$1.parse(color)[channel2]);
};
const channel$1 = channel;
const luminance = (color) => {
  const { r, g, b } = Color$1.parse(color);
  const luminance2 = 0.2126 * _.channel.toLinear(r) + 0.7152 * _.channel.toLinear(g) + 0.0722 * _.channel.toLinear(b);
  return _.lang.round(luminance2);
};
const luminance$1 = luminance;
const isLight = (color) => {
  return luminance$1(color) >= 0.5;
};
const isLight$1 = isLight;
const isDark = (color) => {
  return !isLight$1(color);
};
const isDark$1 = isDark;
const adjustChannel = (color, channel2, amount) => {
  const channels2 = Color$1.parse(color);
  const amountCurrent = channels2[channel2];
  const amountNext = _.channel.clamp[channel2](amountCurrent + amount);
  if (amountCurrent !== amountNext)
    channels2[channel2] = amountNext;
  return Color$1.stringify(channels2);
};
const adjustChannel$1 = adjustChannel;
const lighten = (color, amount) => {
  return adjustChannel$1(color, "l", amount);
};
const lighten$1 = lighten;
const darken = (color, amount) => {
  return adjustChannel$1(color, "l", -amount);
};
const darken$1 = darken;
const adjust = (color, channels2) => {
  const ch = Color$1.parse(color);
  const changes = {};
  for (const c in channels2) {
    if (!channels2[c])
      continue;
    changes[c] = ch[c] + channels2[c];
  }
  return change$1(color, changes);
};
const adjust$1 = adjust;
const mix = (color1, color2, weight = 50) => {
  const { r: r1, g: g1, b: b1, a: a1 } = Color$1.parse(color1);
  const { r: r2, g: g2, b: b2, a: a2 } = Color$1.parse(color2);
  const weightScale = weight / 100;
  const weightNormalized = weightScale * 2 - 1;
  const alphaDelta = a1 - a2;
  const weight1combined = weightNormalized * alphaDelta === -1 ? weightNormalized : (weightNormalized + alphaDelta) / (1 + weightNormalized * alphaDelta);
  const weight1 = (weight1combined + 1) / 2;
  const weight2 = 1 - weight1;
  const r = r1 * weight1 + r2 * weight2;
  const g = g1 * weight1 + g2 * weight2;
  const b = b1 * weight1 + b2 * weight2;
  const a = a1 * weightScale + a2 * (1 - weightScale);
  return rgba$1(r, g, b, a);
};
const mix$1 = mix;
const invert = (color, weight = 100) => {
  const inverse = Color$1.parse(color);
  inverse.r = 255 - inverse.r;
  inverse.g = 255 - inverse.g;
  inverse.b = 255 - inverse.b;
  return mix$1(inverse, color, weight);
};
const invert$1 = invert;
export {
  adjust$1 as a,
  isDark$1 as b,
  channel$1 as c,
  darken$1 as d,
  invert$1 as i,
  lighten$1 as l,
  rgba$1 as r
};
