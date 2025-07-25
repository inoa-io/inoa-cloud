const IP = process.env["IP"] ? process.env["IP"] : "127.0.0.1";

export default [
  {
    context: [
      '/api',
    ],
    // Communicate with Backend in k3s
    "target": "http://inoa." + IP + ".nip.io:8080",
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
