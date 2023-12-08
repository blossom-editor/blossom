function responseText(response) {
  if (!response.ok)
    throw new Error(response.status + " " + response.statusText);
  return response.text();
}
function text(input, init) {
  return fetch(input, init).then(responseText);
}
function parser(type) {
  return (input, init) => text(input, init).then((text2) => new DOMParser().parseFromString(text2, type));
}
var svg = parser("image/svg+xml");
export {
  svg as s
};
