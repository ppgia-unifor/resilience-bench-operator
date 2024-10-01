import { parseHTML } from 'k6/html';
import { check } from 'k6';
import http from 'k6/http';
import { Counter, Trend } from 'k6/metrics';

const creditCard = {
  email: 'someoneelse@example.com',
  street_address: '1321 Washington Soares Avenue',
  zip_code: '98033',
  city: 'Fortaleza',
  state: 'CE',
  country: 'Brazil',
  credit_card_number: '5379759871429836',
  credit_card_expiration_month: '1',
  credit_card_expiration_year: '2034',
  credit_card_cvv: '665',
};

function randomCurrency() {
  const currencies = ['EUR', 'USD', 'JPY', 'CAD', 'GBP', 'TRY'];
  return currencies[Math.floor(Math.random() * currencies.length)];
}

function randomProduct() {
  const products = [
    '0PUK6V6EV0',
    '1YMWWN1N4O',
    '2ZYFJ3GM2N',
    '66VCHSJNUP',
    '6E92ZMYYFZ',
    '9SIQT8TOJO',
    'L9ECAV7KIM',
    'LS4PSXUNUM',
    'OLJCESPC7Z'
  ];
  return products[Math.floor(Math.random() * products.length)];
}

function randomQuantity() {
  return Math.floor(Math.random() * 10) + 1;
}

function randomCurrency() {
  const currencies = ['EUR', 'USD', 'JPY', 'CAD', 'GBP', 'TRY'];
  return currencies[Math.floor(Math.random() * currencies.length)];
}

function randomProduct() {
  const products = [
    '0PUK6V6EV0',
    '1YMWWN1N4O',
    '2ZYFJ3GM2N',
    '66VCHSJNUP',
    '6E92ZMYYFZ',
    '9SIQT8TOJO',
    'L9ECAV7KIM',
    'LS4PSXUNUM',
    'OLJCESPC7Z'
  ];
  return products[Math.floor(Math.random() * products.length)];
}

function randomQuantity() {
  return Math.floor(Math.random() * 10) + 1;
}

const outputPath = `${__ENV.OUTPUT_PATH}.json`;
const HOST = __ENV.HOST || 'frontend';

const httpDurationIndex = new Trend('custom_index_http_req_duration');
const successIndex = new Counter('custom_index_success');
const errorIndex = new Counter('custom_index_error');

const successAd = new Counter('custom_ad_success');
const errorAd = new Counter('custom_ad_error');

const successPayment = new Counter('custom_payment_cart_success');
const errorPayment = new Counter('custom_payment_cart_error');

const successShippingOnCheckout = new Counter('custom_shipping_checkout_success');
const errorShippingOnCheckout = new Counter('custom_shipping_checkout_error');

const successShippingOnCart = new Counter('custom_shipping_cart_success');
const errorShippingOnCart = new Counter('custom_shipping_cart_error');

// const successPaymentOnCart = new Counter('custom_payment_cart_success');
// const errorPaymentOnCart = new Counter('custom_payment_cart_error');

const successRecommendations = new Counter('custom_recommendations_success');
const errorRecommendations = new Counter('custom_recommendations_error');

const httpDurationProduct = new Trend('custom_product_http_req_duration');
const successProduct = new Counter('custom_product_success');
const errorProduct = new Counter('custom_product_error');

const httpDurationCart = new Trend('custom_cart_http_req_duration');
const successCart = new Counter('custom_cart_success');
const errorCart = new Counter('custom_cart_error');

const httpDurationViewCart = new Trend('custom_viewcart_http_req_duration');
const successViewCart = new Counter('custom_viewcart_success');
const errorViewCart = new Counter('custom_viewcart_error');

const httpDurationCurrency = new Trend('custom_currency_http_req_duration');
const successCurrency = new Counter('custom_currency_success');
const errorCurrency = new Counter('custom_currency_error');

const httpDurationCheckout = new Trend('custom_checkout_http_req_duration');
const successCheckout = new Counter('custom_checkout_success');
const errorCheckout = new Counter('custom_checkout_error');

export default function () {
  index();
  browseProduct();
  addToCart();
  viewCart();
  setCurrency();
  checkout();
}

function index() {
  const res = http.get(`http://${HOST}/`);
  const successfulIndex = res.status === 200 ? 1 : 0;
  httpDurationIndex.add(res.timings.duration);
  successIndex.add(successfulIndex ? 1 : 0);
  errorIndex.add(successfulIndex ? 0 : 1);
}

function browseProduct() {
  const res = http.get(`http://${HOST}/product/${randomProduct()}`);
  const body = parseHTML(res.body);

  const ad = body.find('body > main > div.ad > div > div > strong');
  successAd.add(ad.text() === 'Ad' ? 1 : 0);
  errorAd.add(ad.text() === 'Ad' ? 0 : 1);

  const recommendation = body.find('body > main > div:nth-child(2) > section.recommendations');
  const successfulRecommentations = recommendation.text() !== '';
  successRecommendations.add(successfulRecommentations ? 1 : 0);
  errorRecommendations.add(successfulRecommentations ? 0 : 1);

  const successfulProduct = res.status === 200;
  httpDurationProduct.add(res.timings.duration);
  successProduct.add(successfulProduct ? 1 : 0);
  errorProduct.add(successfulProduct ? 0 : 1);
}

function addToCart() {
  const body = { product_id: randomProduct(), quantity: randomQuantity() };
  const res = http.post(`http://${HOST}/cart`, body);

  const errorShipping = res.body.includes('failed to get shipping quote');
  successShippingOnCart.add(errorShipping ? 0 : 1);
  errorShippingOnCart.add(errorShipping ? 1 : 0);

  httpDurationCart.add(res.timings.duration);
  successCart.add(res.status === 200 ? 1 : 0);
  errorCart.add(res.status !== 200 ? 1 : 0);
}

function viewCart() {
  const res = http.get(`http://${HOST}/cart`);

  const errorShipping = res.body.includes('failed to get shipping quote');

  successShippingOnCart.add(errorShipping ? 0 : 1);
  errorShippingOnCart.add(errorShipping ? 1 : 0);
  httpDurationViewCart.add(res.timings.duration);
  successViewCart.add(res.status === 200 ? 1 : 0);
  errorViewCart.add(res.status !== 200 ? 1 : 0);
}

function checkout() {
  const res = http.post(`http://${HOST}/cart/checkout`, creditCard);
  check(res, {
    'checkout is 200': (r) => r.status === 200,
  });

  if (res.status === 200) {
    const errorShipping = res.body.includes('failed to get shipping quote');
    const errorPaymentFlag = res.body.includes('failed to charge card');

    successShippingOnCheckout.add(errorShipping ? 0 : 1);
    errorShippingOnCheckout.add(errorShipping ? 1 : 0);
    successPayment.add(errorPayment ? 0 : 1);
    errorPayment.add(errorPaymentFlag ? 1 : 0);
    httpDurationCheckout.add(res.timings.duration);
    successCheckout.add(res.status === 200 ? 1 : 0);
    errorCheckout.add(res.status !== 200 ? 1 : 0);
  } else {
    console.error(`Failed to checkout: ${res.status}`, res.body);
    errorShippingOnCheckout.add(1);
    errorPayment.add(1);
    errorCheckout.add(1);
  }
}

function setCurrency() {
  const res = http.post(`http://${HOST}/setCurrency`, { currency_code: randomCurrency() });
  httpDurationCurrency.add(res.timings.duration);
  successCurrency.add(res.status === 200 ? 1 : 0);
  errorCurrency.add(res.status !== 200 ? 1 : 0);
}

export function handleSummary(data) {
  const summary = {};
  const metrics = {};

  for (const [metricName, metricData] of Object.entries(data.metrics)) {
    if (metricName.startsWith('custom_')) {
      const cleanName = metricName.replace('custom_', '');
      metrics[cleanName] = metricData.type === 'trend'
        ? metricData.values.med
        : metricData.values.count;
    }
  }

  for (const metricName of Object.keys(metrics)) {
    if (metricName.endsWith('success')) {
      const page = metricName.replace('_success', '');
      const rate = metrics[metricName] / (metrics[metricName] + metrics[`${page}_error`]);
      metrics[`${page}_success_rate`] = rate;
    }
  }

  Object.assign(metrics, {
    iterations: data.metrics.iterations.values.count,
    http_reqs: data.metrics.http_reqs.values.count,
    iteration_duration: data.metrics.iteration_duration.values.med,
  });

  summary[outputPath] = JSON.stringify(sortObjectKeys(metrics), null, 2);
  console.log(`checkout_success_rate=${metrics.checkout_success_rate}`);
  return summary;
}

function sortObjectKeys(obj) {
  if (typeof obj !== "object" || obj === null || Array.isArray(obj)) {
    return obj; // Return the value if obj is not an object or is an array
  }

  const sortedKeys = Object.keys(obj).sort();
  const sortedObj = {};

  for (let key of sortedKeys) {
    sortedObj[key] = sortObjectKeys(obj[key]);
  }

  return sortedObj;
}