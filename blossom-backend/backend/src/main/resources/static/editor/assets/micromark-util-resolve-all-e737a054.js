function resolveAll(constructs, events, context) {
  const called = [];
  let index = -1;
  while (++index < constructs.length) {
    const resolve = constructs[index].resolveAll;
    if (resolve && !called.includes(resolve)) {
      events = resolve(events, context);
      called.push(resolve);
    }
  }
  return events;
}
export {
  resolveAll as r
};
