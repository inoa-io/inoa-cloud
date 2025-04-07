const INOA_DOMAIN = process.env["INOA_DOMAIN"] ? process.env["INOA_DOMAIN"] : "127.0.0.1.nip.io";

export default [
  {
    context: [
      '/api',
    ],
    // Communicate with Backend in k3s
    "target": "http://inoa." + INOA_DOMAIN + ":8080",
    // Communicate with locally run Backend via Micronaut
    // "target": "http://localhost:4300",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  }
];
