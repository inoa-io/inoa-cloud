const INOA_IP = process.env["INOA_IP"] ? process.env["INOA_IP"] : "127.0.0.1";


export default [
  {
    context: [
      '/api',
    ],
    "target": "http://inoa." + INOA_IP + ".nip.io:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  }
];
