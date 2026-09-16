const SHOP_URL = '/api/store';

async function loadStore() {
  const res = await fetch(SHOP_URL);
  const data = await res.json();

  document.getElementById('store-name').textContent = data.storeName;
  document.getElementById('store-tagline').textContent = data.tagline;
  document.getElementById('promo-box').textContent = data.banner;

  const productGrid = document.getElementById('product-grid');
  const productHtml = data.products.map((product) => `
    <article class="product-card">
      <div class="product-image">${product.image}</div>
      <div class="product-body">
        <div class="product-top">
          <div class="product-name">${product.name}</div>
          ${product.topSeller ? '<span class="product-badge">Top seller</span>' : ''}
        </div>
        <div class="product-meta">
          <span>${product.category}</span>
          <span>SKU ${product.id}</span>
        </div>
        <div class="product-price">₹${product.price}</div>
        <div class="product-footer">
          <span class="stock ${product.stock > 0 ? '' : 'out'}">${product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}</span>
          <button>Add to cart</button>
        </div>
      </div>
    </article>
  `).join('');

  productGrid.innerHTML = productHtml;

  const recommendationBox = document.getElementById('recommendation-box');
  recommendationBox.innerHTML = `
    <div class="rec-item">
      <strong>Frequently bought with your last order</strong>
      <span class="rec-score">91%</span>
    </div>
    <div class="rec-item">
      <strong>Trending in Fitness</strong>
      <span class="rec-score">87%</span>
    </div>
    <div class="rec-item">
      <strong>Because you viewed similar Electronics</strong>
      <span class="rec-score">79%</span>
    </div>
  `;

  updateServiceStatus();
}

async function updateServiceStatus() {
  const services = [
    { key: 'eureka', id: 'eureka-status' },
    { key: 'gateway', id: 'gateway-status' },
    { key: 'products', id: 'products-status' }
  ];

  for (const service of services) {
    try {
      const res = await fetch(`/api/services/${service.key}`);
      const payload = await res.json();
      const status = payload.status || 'DOWN';
      const element = document.getElementById(service.id);
      element.textContent = status;
      element.style.color = status === 'UP' ? '#16a34a' : '#dc2626';
    } catch (error) {
      const element = document.getElementById(service.id);
      element.textContent = 'DOWN';
      element.style.color = '#dc2626';
    }
  }
}

window.addEventListener('DOMContentLoaded', loadStore);

