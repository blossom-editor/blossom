const { generateService } = require('@umijs/openapi');
const { openApi } = require('./config.ts');

// Async function to generate services for each OpenAPI configuration
async function run() {
  // Iterate over openApi configurations
  for (const api of openApi) {
    // Generate service for current OpenAPI configuration
    await generateService(api);
  }
}

// Run the function
run();

// function customTypeName(data) {
//   console.log("data", data)
//   return data.operationId
// }
//
// function customClassName(tagName){
//   console.log("tagName", tagName)
//   return tagName
// }
//
// function customFileNames(operationObject, apiPath,_apiMethod) {
//   const operationId = operationObject.operationId
//   if (!operationId) {
//     console.warn('[Warning] no operationId', apiPath)
//   }
//   const res = operationId.split('_')
//   if (res.length > 1) {
//     console.log("res1111111111111111", res)
//     res.shift()
//     if (res.length > 2) {
//       console.warn('[Warning]  operationId has more than 2 part', apiPath)
//     }
//     console.log("res.join('_')", res.join('_'))
//     return [res.join('_')]
//   } else {
//     const controllerName = (res || [])[0]
//     if (controllerName) {
//       console.log("controllerName", controllerName)
//       const splitName = controllerName.split('-')
//       // 去除splitName的第一个和最后一个元素
//       const res = [splitName[1], splitName[2]]
//       console.log("res", splitName[0]+'/'+res.join('-'))
//       return [splitName[0]]
//     }
//     console.log("controllerName1111111", controllerName)
//     return [];
//   }
// }
//
//
// function customFunctionName(data) {
//   let res = data.operationId.split('-').slice(1);
//   return res.join('-');
// }