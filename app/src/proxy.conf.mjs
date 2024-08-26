export default [
  {
    context: [
      '/api',
    ],
    "target": "http://inoa:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  }
];
