const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const ROOT = __dirname;
const DATA_FILE = path.join(ROOT, 'data', 'dashboard.json');
const STORE_FILE = path.join(ROOT, 'data', 'store.json');

const BACKEND_TARGETS = {
  eureka: 'http://localhost:8761',
  gateway: 'http://localhost:8080/gateway/products/top-sellers',
  products: 'http://localhost:8081/api/products/top-sellers',
  inventory: 'http://localhost:8082/api/inventory',
  recommendation: 'http://localhost:8083/api/recommendations/U100'
};

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify(payload, null, 2));
}

function serveFile(res, filePath) {
  fs.readFile(filePath, (err, data) => {
    if (err) {
      sendJson(res, 404, { error: 'File not found' });
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentTypes = {
      '.html': 'text/html; charset=utf-8',
      '.css': 'text/css; charset=utf-8',
      '.js': 'application/javascript; charset=utf-8',
      '.json': 'application/json; charset=utf-8',
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.svg': 'image/svg+xml'
    };

    res.writeHead(200, { 'Content-Type': contentTypes[ext] || 'text/plain; charset=utf-8' });
    res.end(data);
  });
}

function proxyToBackend(url, res) {
  const target = new URL(url);

  const request = http.get(target, (backendRes) => {
    let body = '';

    backendRes.on('data', (chunk) => {
      body += chunk;
    });

    backendRes.on('end', () => {
      try {
        const json = JSON.parse(body);
        sendJson(res, backendRes.statusCode || 200, {
          status: json.servedBy || 'UP',
          payload: json
        });
      } catch (error) {
        res.writeHead(backendRes.statusCode || 200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ status: 'UP', payload: body }));
      }
    });
  });

  request.on('error', () => {
    sendJson(res, 503, { status: 'DOWN', message: 'Backend service unavailable' });
  });
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost:' + PORT);

  if (url.pathname === '/api/dashboard') {
    fs.readFile(DATA_FILE, 'utf8', (err, data) => {
      if (err) {
        sendJson(res, 500, { error: 'dashboard.json not found' });
        return;
      }
      sendJson(res, 200, JSON.parse(data));
    });
    return;
  }

  if (url.pathname === '/api/store') {
    fs.readFile(STORE_FILE, 'utf8', (err, data) => {
      if (err) {
        sendJson(res, 500, { error: 'store.json not found' });
        return;
      }
      sendJson(res, 200, JSON.parse(data));
    });
    return;
  }

  if (url.pathname.startsWith('/api/services/')) {
    const key = url.pathname.split('/').filter(Boolean).pop();
    const target = BACKEND_TARGETS[key];
    if (!target) {
      sendJson(res, 404, { status: 'DOWN', message: 'Unknown service key' });
      return;
    }
    proxyToBackend(target, res);
    return;
  }

  const filePath = url.pathname === '/' ? path.join(ROOT, 'index.html') : path.join(ROOT, url.pathname.replace(/^\//, ''));

  if (filePath.startsWith(ROOT) && fs.existsSync(filePath) && !fs.statSync(filePath).isDirectory()) {
    serveFile(res, filePath);
    return;
  }

  sendJson(res, 404, { error: 'Route not found' });
});

server.listen(PORT, () => {
  console.log(`Frontend dashboard running at http://localhost:${PORT}`);
  console.log('Demo data is served from frontend/data/dashboard.json');
});
