const path = require('path');
const schemaPath = path.resolve(__dirname, 'openapi.json');

module.exports = {
  openApi: [
    {
      // requestLibPath: 'import request from "umi-request";', // 想怎么引入封装请求方法
      schemaPath: schemaPath, // openAPI规范地址(此处必须绝对路径)
      serversPath: './src/autoApi', // 生成代码到哪个目录
      // hook: {
      //
      //   customClassName:customClassName,
      //   customTypeName:customTypeName,
      // },
      namespace: 'BlossomAPI',
      // mockFolder: './src/autoApi/mock',
    }
  ],
};

