export default [
  {
    context: [
      '/api',
    ],
    "target": "http://inoa.default:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  }
];
