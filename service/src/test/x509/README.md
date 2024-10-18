# Create certificate & key

```sh
rm -rf key.pem cert.pem
openssl req -x509 -sha256 -subj '/CN=localhost' -days 36500 -newkey rsa:1024 -keyout key.pem -out cert.pem -noenc
```

