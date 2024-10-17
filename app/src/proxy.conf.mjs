const INOA_DOMAIN = process.env["INOA_DOMAIN"] ? process.env["INOA_DOMAIN"] : "127.0.0.1.nip.io";

export default [
  {
    context: [
      '/api',
    ],
    "target": "http://inoa." + INOA_DOMAIN + ":8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  }
];
