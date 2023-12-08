function colors(specifier) {
  var n = specifier.length / 6 | 0, colors2 = new Array(n), i = 0;
  while (i < n)
    colors2[i] = "#" + specifier.slice(i * 6, ++i * 6);
  return colors2;
}
const schemeCategory10 = colors("1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf");
const schemeTableau10 = colors("4e79a7f28e2ce1575976b7b259a14fedc949af7aa1ff9da79c755fbab0ab");
export {
  schemeTableau10 as a,
  schemeCategory10 as s
};
