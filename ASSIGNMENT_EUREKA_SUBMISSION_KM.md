# របាយការណ៍ស្រាវជ្រាវ និងអនុវត្ត
## Spring Cloud Eureka Service Discovery និង Client-Side Load Balancing

**គម្រោង:** ISTAD ITE E-Commerce Platform  
**អ្នករៀបចំ:** Leng Saroth (`it-roth`)  
**បច្ចេកវិទ្យា:** Java 25, Spring Boot 4.1.2, Spring Cloud Netflix Eureka, Spring Cloud LoadBalancer, Gradle 9.7  
**Repository:** https://github.com/it-roth/istad-ite-platform

> មុនដាក់ស្នើ សូមពិនិត្យឈ្មោះ អ្នកនិពន្ធ និង URL repository ឱ្យត្រូវនឹងព័ត៌មានផ្ទាល់ខ្លួនរបស់អ្នក។ រូបភាពត្រូវបញ្ចូលពីការសាកល្បងពិត មិនត្រូវទុកសញ្ញាណែនាំដាក់រូបក្នុងឯកសារចុងក្រោយទេ។

## សេចក្តីសង្ខេប

គម្រោងនេះបង្ហាញការទំនាក់ទំនងរវាង microservices ពីររបៀប៖

1. កំណត់ Service URL ដោយផ្ទាល់ (Hard-Coded Service Addresses)។
2. ប្រើ Eureka សម្រាប់ Service Registration និង Discovery ហើយប្រើ Spring Cloud LoadBalancer ដើម្បីជ្រើសរើស instance តាមឈ្មោះ service។

Order Service ជាអ្នកសម្របសម្រួលការបង្កើត order៖ ពិនិត្យ Customer, ពិនិត្យ Business និងផលិតផល, ហៅ Payment Service, បន្ទាប់មករក្សាទុក Order។ Order Service មាន endpoint សម្រាប់សាកល្បងការហៅ service តាម URL ផ្ទាល់ និងតាម Eureka។

### ប្លង់ប្រព័ន្ធ និង Port

| Service | ឈ្មោះក្នុង Eureka | Port |
|---|---|---:|
| Discovery Service (Eureka Server) | `discovery-service` | 8761 |
| Customer Service | `customer-service` | 9091 |
| Order Service | `order-service` | 9092 |
| Business Service | `business-service` | 9093 |
| Payment Service | `payment-service` | 9094 |

```text
Customer Service ─┐
Business Service ─┼── Eureka Server :8761
Payment Service ──┤
Order Service ────┘

Order Service ──> Customer Service
              ├─> Business Service
              └─> Payment Service
```

## Part 1: ការទំនាក់ទំនងដោយ Hard-Coded Service Addresses

### 1.1 គោលការណ៍

ក្នុងរបៀបនេះ Order Service ប្រើ URL ដែលកំណត់ទុកក្នុង configuration ដើម្បីហៅ service ផ្សេងៗ៖

```text
Customer: http://localhost:9091
Business: http://localhost:9093
Payment:  http://localhost:9094
```

Order Service បន្ថែម path API ទៅ URL ខាងលើ ដូចជា `GET /api/customers/{id}`, `GET /api/businesses/{id}` និង `POST /api/payments`។ របៀបនេះងាយយល់ និងអាចប្រើសម្រាប់ demo ក្នុង local environment ប៉ុន្តែ caller ត្រូវដឹងទីតាំងរបស់ service ជាមុន។

### 1.2 វិធីសាកល្បង

ចាប់ផ្តើម Discovery, Customer, Business, Payment និង Order Service រួចសាកល្បង៖

```text
GET http://localhost:9092/api/orders/communication-test/hard-coded
```

លទ្ធផលពេលសាកល្បង៖ HTTP `200`; `allServicesReachable` មានតម្លៃ `true`; Customer, Business និង Payment បានឆ្លើយតបដោយ HTTP `200`។

ទម្រង់ response រួមមាន `communicationMode`, `allServicesReachable`, `totalDurationMs`, `serviceChecks` និង `summary`។ ក្នុង `serviceChecks` នីមួយៗមានឈ្មោះ service, URL, ស្ថានភាពជោគជ័យ, HTTP status code និង response time។ Response body អាចខុសគ្នាតាមទិន្នន័យដែលមានក្នុង database នៅពេលសាកល្បង។

**រូបភាពបញ្ចូលនៅទីនេះ:** Screenshot ពិតពី Postman ឬ terminal ដែលបង្ហាញ request ខាងលើ, HTTP 200 និង `allServicesReachable: true`។

### 1.3 គុណសម្បត្តិ និងដែនកំណត់

**គុណសម្បត្តិ**

- កំណត់រចនាសម្ព័ន្ធងាយស្រួល និងសមស្របសម្រាប់ demo ឬប្រព័ន្ធតូច។
- មិនចាំបាច់ដំឡើង Service Registry បន្ថែម ដើម្បី resolve URL ទេ។

**ដែនកំណត់**

- **Tight coupling:** Caller ត្រូវដឹង host និង port របស់ service គោលដៅ។
- **ពិបាក scale:** URL មួយមិនស្វែងរក ឬជ្រើសរើស instance ថ្មីដោយស្វ័យប្រវត្តិទេ។
- **ការផ្លាស់ប្តូរ configuration:** ប្តូរ port, host ឬ environment អាចតម្រូវឱ្យកែ config និង restart service ដែលហៅ។
- **គ្មានការជ្រើសរើស instance ដោយស្វ័យប្រវត្តិ:** គ្មាន client-side load balancing ពី URL ថេរមួយ។
- **Failure របស់គោលដៅ:** បើ host/port ដែលបានកំណត់មិនអាចទាក់ទងបាន request នឹងបរាជ័យ លុះត្រាតែមាន retry/fallback ផ្សេង។
- **ភាពខុសគ្នារវាង environment:** Local, test និង production អាចត្រូវការ URL ខុសៗគ្នា។

## Part 2: ស្រាវជ្រាវអំពី Spring Cloud Netflix Eureka

### 2.1 គោលបំណង

Eureka ជា Service Registry។ Service instance ចុះឈ្មោះឈ្មោះ service, host, port និងព័ត៌មាន instance ទៅ Eureka Server។ Service ដែលត្រូវហៅ instance ផ្សេងអាចស្វែងរកវាតាម service ID ជំនួសឱ្យការសរសេរ IP និង port របស់ instance នោះដោយផ្ទាល់។

Spring Cloud Netflix ផ្តល់ integration សម្រាប់ Eureka ក្នុង Spring applications។ Eureka Client ផ្ញើ registration និង heartbeat ហើយទាញយកព័ត៌មាន registry ដើម្បីឱ្យ application អាចរកឃើញ service ផ្សេងៗ។ Eureka Server ត្រូវបានកំណត់ឱ្យនៅ port `8761` ក្នុងគម្រោងនេះ។

### 2.2 ដំណើរការសំខាន់ៗ

1. **Startup និង registration:** Service ចាប់ផ្តើម ហើយចុះឈ្មោះឈ្មោះ, host និង port ជាមួយ Eureka Server។
2. **Heartbeat:** Instance ផ្ញើ heartbeat ជាប្រចាំ ដើម្បីបញ្ជាក់ថានៅតែដំណើរការ។
3. **Registry fetch:** Eureka Client ទាញ និង cache បញ្ជី instance ដើម្បីប្រើពេល resolve service។
4. **Service discovery:** Order Service ស្នើ `customer-service`, `business-service` ឬ `payment-service` ជាឈ្មោះ logical។
5. **Load balancing:** Spring Cloud LoadBalancer ជ្រើសរើស instance ពីបញ្ជីដែលបានរកឃើញ ហើយ WebClient ផ្ញើ HTTP request ទៅ instance នោះ។
6. **Deregistration/expiration:** ពេល graceful shutdown client អាចដក registration; ពេលបាត់ heartbeat ដោយមិនបានបិទត្រឹមត្រូវ ការសម្គាល់ថា instance មិនមានអាចយឺតតាម lease និង eviction configuration។

Eureka Client ប្រើ registry ដែល cache ក្នុង local memory។ ដូច្នេះការរកឃើញ instance មិនមែនជាការសួរ Eureka Server រាល់ HTTP request ទេ។ ការផ្លាស់ប្តូរ registry អាចត្រូវការពេលមួយ មុនពេល client ទទួលព័ត៌មានថ្មី។

### 2.3 Cache និង Self-Preservation

Eureka Server ប្រើ registry និង response cache ដើម្បីបម្រើព័ត៌មាន instance។ Cache ជួយកាត់បន្ថយការងារពេលអាន registry ប៉ុន្តែអាចបង្កើតភាពយឺតយ៉ាវរវាងការផ្លាស់ប្តូរ និងការមើលឃើញការផ្លាស់ប្តូរនោះពី client។

Self-Preservation ជួយការពារ Eureka មិនឱ្យដក instances ច្រើនចេញក្នុងពេលមាន network disruption ដែលធ្វើឱ្យ heartbeat ធ្លាក់ចុះ។ ផលប៉ះពាល់គឺ instance ដែលឈប់ដំណើរការពិតអាចនៅក្នុង registry មួយរយៈ។ ត្រូវពិចារណា lease, eviction, health reporting និង network conditions ជាមួយគ្នា។

Eureka ជ្រើសរើសឱ្យ registry និង clients បន្តបម្រើព័ត៌មានដែលមានក្នុងពេលមានការរំខាន ជាជាងធានា consistency ភ្លាមៗគ្រប់ node។ ដូច្នេះ client អាចប្រើ registry ដែលមិនទាន់ទាន់សម័យបាន។

### 2.4 គុណសម្បត្តិ និងដែនកំណត់

**គុណសម្បត្តិ**

- Instance ចុះឈ្មោះ និងស្វែងរកគ្នាតាម service name។
- អាចបន្ថែម instance ដោយមិនចាំបាច់ប្តូរ URL នៅក្នុង caller។
- អាចប្រើជាមួយ Spring Cloud LoadBalancer សម្រាប់ client-side instance selection។
- Client អាចប្រើ registry cache បណ្ដោះអាសន្ន នៅពេល registry មានបញ្ហាបណ្តោះអាសន្ន។

**ដែនកំណត់**

- ត្រូវដំណើរការ និងថែទាំ Eureka Server។
- Registry cache និង heartbeat បង្កើត eventual consistency និងការយឺតក្នុងការរកឃើញការផ្លាស់ប្តូរ។
- Self-preservation អាចទុក instance ដែលមិនអាចប្រើបានក្នុង registry ពេលមានបញ្ហាបណ្តាញ។
- Client ត្រូវមាន discovery/load-balancing integration និងការកំណត់ត្រឹមត្រូវ។
- Eureka ជា registry មិនមែនជា circuit breaker ឬ API gateway ទេ។ ការការពារ retry, timeout, circuit breaking និង routing ត្រូវការយន្តការបន្ថែម។

## Part 3: ការដំឡើង Eureka និង Load Balancing ក្នុងគម្រោង

### 3.1 Eureka Server

គម្រោងមាន module `discovery-service` ដែលដំណើរការ Eureka Server នៅ `http://localhost:8761`។ Server មិនចុះឈ្មោះខ្លួនជាមួយ registry របស់ខ្លួនទេ ហើយទទួល registration ពី microservices ផ្សេងៗ។

### 3.2 Eureka Clients

Customer, Business, Payment និង Order Services មានឈ្មោះ application និង Eureka client configuration។ នៅពេល services ដំណើរការ Eureka dashboard បង្ហាញ `CUSTOMER-SERVICE`, `BUSINESS-SERVICE`, `PAYMENT-SERVICE` និង `ORDER-SERVICE` ជា instances ដែលបានចុះឈ្មោះ។

**រូបភាពបញ្ចូលនៅទីនេះ:** Screenshot ពិតពី `http://localhost:8761` បង្ហាញ services ដែលកំពុងមានស្ថានភាព UP។ ត្រូវប្រាកដថា screenshot ថតនៅពេល services ទាំងអស់កំពុងដំណើរការ។

### 3.3 Spring Cloud LoadBalancer និង WebClient

Order Service មាន WebClient ពីរ៖ client សម្រាប់ URL ផ្ទាល់ និង client ដែលបានកំណត់ `@LoadBalanced` សម្រាប់ service name។ ការកំណត់ URL-ផ្ទាល់នៅក្នុង YAML៖

```yaml
microservices:
  hardcoded:
    customer-url: http://localhost:9091
    business-url: http://localhost:9093
    payment-url: http://localhost:9094
  eureka:
    customer-url: http://customer-service
    business-url: http://business-service
    payment-url: http://payment-service
```

នៅក្នុងរបៀប Eureka WebClient ប្រើ logical service name។ Spring Cloud LoadBalancer និង Eureka Discovery Client រក instance ហើយជ្រើសរើស instance សម្រាប់ request។ ក្នុងរបៀប hard-coded WebClient ប្រើ URL `localhost` ដែលបានកំណត់។

### 3.4 លទ្ធផលសាកល្បង Communication

សាកល្បង endpoints ទាំងពីរខណៈពេល services ដំណើរការ៖

```text
GET http://localhost:9092/api/orders/communication-test/hard-coded
GET http://localhost:9092/api/orders/communication-test/eureka
```

ក្នុងការផ្ទៀងផ្ទាត់ runtime ដែលបានធ្វើ៖

- Health endpoint របស់ Eureka, Customer, Order, Business និង Payment បានឆ្លើយតប HTTP `200`។
- Hard-coded communication endpoint បានឆ្លើយតប HTTP `200`; Customer, Business និង Payment មាន `success: true`។
- Eureka communication endpoint បានឆ្លើយតប HTTP `200`; Customer, Business និង Payment មាន `success: true`។

**រូបភាពបញ្ចូលនៅទីនេះ:** Screenshot ពិតនៃ Eureka communication endpoint ដែលបង្ហាញ URL ជា `http://customer-service`, `http://business-service` និង `http://payment-service` និងលទ្ធផលជោគជ័យ។

### 3.5 ការប្រៀបធៀប

| លក្ខណៈ | Hard-Coded URLs | Eureka + LoadBalancer |
|---|---|---|
| របៀបដាក់អាសយដ្ឋាន | Host និង port ជាក់លាក់ | Logical service name |
| ការស្វែងរក instance | Caller ដឹង URL ជាមុន | Eureka registry និង client cache |
| ការបន្ថែម instance | ត្រូវគ្រប់គ្រង URL/Proxy ដោយខ្លួនឯង | Instance ចុះឈ្មោះ ហើយ LoadBalancer អាចជ្រើសរើស |
| ការពឹងផ្អែក | ពឹងផ្អែកលើ network address | ពឹងផ្អែកលើ service ID និង registry |
| ការដំឡើង | ងាយស្រួលសម្រាប់ demo | ត្រូវមាន Eureka និង client configuration |
| ដែនកំណត់ | URL អាច stale ឬបរាជ័យ | Registry cache ក៏អាច stale និងបង្កើត eventual consistency |

## Part 4: ការពិសោធន៍ និងការផ្ទៀងផ្ទាត់

ផ្នែកនេះបែងចែករវាងអ្វីដែលបានផ្ទៀងផ្ទាត់ runtime និងអ្វីដែលត្រូវធ្វើពិសោធន៍បន្ថែម។ កុំសរសេរថាបានធ្វើពិសោធន៍ ប្រសិនបើមិនបានដំណើរការ និងថតលទ្ធផលពិត។

### 4.1 Health និង Registration

បានផ្ទៀងផ្ទាត់ថា health endpoints របស់ service ទាំងប្រាំឆ្លើយតប HTTP `200` នៅពេលពិនិត្យ។ Eureka registry ក៏បង្ហាញ Customer, Business, Payment និង Order ជា registered services នៅពេលពិនិត្យ។

លទ្ធផលនេះបញ្ជាក់ថា services កំពុងដំណើរការ និងបានចុះឈ្មោះនៅពេលសាកល្បង។ វាមិនមែនជាភស្តុតាងថា Eureka នឹងប្តូរ status ទៅ DOWN ពេល database បរាជ័យទេ ព្រោះ configuration `eureka.client.healthcheck.enabled` មិនត្រូវបានបញ្ជាក់ថាបានបើកក្នុង service configuration បច្ចុប្បន្ន។

### 4.2 Graceful Shutdown និង Sudden Crash

មិនបានបញ្ចប់ការពិសោធន៍បិទ service ដោយ graceful shutdown និង sudden process termination ក្នុងការផ្ទៀងផ្ទាត់នេះទេ។ អាចបំពេញផ្នែកនេះដោយ៖

1. ថតស្ថានភាព Eureka dashboard មុនបិទ service។
2. បិទ instance មួយតាម Stop button/normal shutdown ហើយកត់ត្រាពេលវេលាដែល instance បាត់ពី registry។
3. ចាប់ផ្តើមឡើងវិញ រួចបញ្ឈប់ process ដោយមិនឱ្យ shutdown hook ដំណើរការ។
4. សង្កេតថា instance អាចនៅក្នុង registry រហូតដល់ lease expiration/eviction។
5. បញ្ចូលតែពេលវេលា និង screenshot ដែលបានសង្កេតពិត។

ជាទូទៅ graceful shutdown អាចផ្ញើ deregistration; sudden crash មិនអាចផ្ញើ deregistration ដូច្នេះ Eureka ពឹងលើ heartbeat, lease និង eviction។ លទ្ធផលពិតអាស្រ័យលើ configuration និងបរិស្ថានសាកល្បង។

### 4.3 Eureka Health Reporting

Health endpoint HTTP `200` ត្រូវបានផ្ទៀងផ្ទាត់។ ប៉ុន្តែការបង្ហាញ health status នៅ Eureka នៅពេល database មិនដំណើរការ មិនទាន់បានសាកល្បង។ បើ assignment តម្រូវការពិសោធន៍នេះ ត្រូវពិនិត្យ និងកំណត់ Eureka health-check integration ជាមុន រួចសាកល្បង database unavailable និងថតលទ្ធផល dashboard។

### 4.4 Scale-Out និង Load Balancing

Communication តាម Eureka ទៅ service instances បច្ចុប្បន្នបានជោគជ័យ។ មិនទាន់មានភស្តុតាងពីការដំណើរការ Payment Service ច្រើន instances ក្នុងពេលតែមួយ និងមិនទាន់បានបញ្ជាក់ថា request ត្រូវបានចែកចាយរវាង instances ទាំងនោះទេ។ ដូច្នេះ កុំអះអាងថា round-robin ឬ scale-out ត្រូវបានផ្ទៀងផ្ទាត់ លុះត្រាតែបានធ្វើតេស្ត។

ដើម្បីបំពេញពិសោធន៍នេះ៖

1. ចាប់ផ្តើម Payment Service ពីរ instances នៅ port ខុសគ្នា។
2. ពិនិត្យ Eureka dashboard ថា `PAYMENT-SERVICE` មាន instances ទាំងពីរ និងស្ថានភាព UP។
3. ផ្ញើ requests ច្រើនតាម Order Service ទៅ Payment Service។
4. ប្រើ log ឬ response header ដែលបានបន្ថែមសម្រាប់ការធ្វើតេស្ត ដើម្បីកំណត់ថា instance ណាឆ្លើយតប។
5. ថត dashboard និងលទ្ធផល requests។ កុំបញ្ចូល `X-Served-By` ជាលទ្ធផល បើ header នោះមិនមានក្នុង code ដែលបានសាកល្បង។

## Part 5: Order Creation និង End-to-End API Test

### 5.1 Workflow

```text
Client
  -> POST /api/orders
  -> Order Service ពិនិត្យ Customer
  -> Order Service ពិនិត្យ Business និងផលិតផល
  -> Order Service ផ្ញើ payment request
  -> Order Service រក្សាទុក Order
  -> Client ទទួល orderId និង trackingId
  -> GET /api/orders/{trackingId} សម្រាប់តាមដាន Order
```

របៀបស្វែងរក service សម្រាប់ការបង្កើត order អាចកំណត់ក្នុង request ជា `EUREKA` ឬ `HARD_CODED`។ API request ត្រូវប្រើ `address` object មួយ៖

```json
{
  "customerId": "b4bd5de2-6496-4b8d-9fc9-5904880ef215",
  "businessId": "e37faf5a-60ee-452f-8d5e-5f4233cf94ec",
  "discoveryMode": "EUREKA",
  "address": {
    "street": "Assignment test street",
    "postalCode": "12000",
    "city": "Phnom Penh"
  },
  "items": [
    {
      "productId": "0fb10a25-6bba-4505-b710-92784193dfca",
      "quantity": 1,
      "price": 1.50
    }
  ]
}
```

UUID ខាងលើជាទិន្នន័យដែលមាននៅក្នុង environment ពេល runtime test។ ប្រសិនបើទិន្នន័យរបស់អ្នកបានផ្លាស់ប្តូរ សូមយក UUID ថ្មីពី Customer និង Business APIs មុនសាកល្បង។

PowerShell command:

```powershell
$body = Get-Content .\order-request.json -Raw
Invoke-RestMethod -Method Post `
  -Uri http://localhost:9092/api/orders `
  -ContentType 'application/json' `
  -Body $body
```

ដើម្បីប្រើ Hard-Coded URLs សូមប្តូរ `"discoveryMode": "EUREKA"` ទៅ `"discoveryMode": "HARD_CODED"`។

### 5.2 លទ្ធផល End-to-End ដែលបានផ្ទៀងផ្ទាត់

ក្នុង runtime test លើ build ដែលបាន refactor៖

- Order creation បានជោគជ័យទាំង `HARD_CODED` និង `EUREKA`។
- Response បានបង្ហាញ `orderStatus: PAID` និង `paymentStatus: COMPLETED`។
- ការហៅ track endpoint ដោយ `trackingId` បានត្រឡប់ Order ដែលបានរក្សាទុក និង status `PAID`។

Response មាន fields ដូចជា `orderId`, `trackingId`, `customerId`, `businessId`, `totalPrice`, `orderStatus`, `paymentId`, `paymentStatus`, `communicationModeUsed`, `deliveryAddress`, `items` និង `createdAt`។ IDs និង timestamp ត្រូវបានបង្កើតថ្មីរាល់ request។

**រូបភាពបញ្ចូលនៅទីនេះ:** Screenshot ពិតបង្ហាញ POST response HTTP 201 និង `orderStatus: PAID`, `paymentStatus: COMPLETED`។  
**រូបភាពបញ្ចូលនៅទីនេះ:** Screenshot ពិតបង្ហាញ GET tracking endpoint និង status របស់ order ដូចគ្នា។

### 5.3 កំណត់ចំណាំអំពី Payment Demo

Payment endpoint ក្នុង code បច្ចុប្បន្នបង្កើត response ស្ថានភាព `COMPLETED` សម្រាប់ demo ហើយរក្សាទុក payment ក្នុង memory របស់ application process។ វាមិនមែនជាការទូទាត់ប្រាក់ពិត ឬការផ្ទុក payment ជា durable database ទេ។ ក្នុងរបាយការណ៍ សូមពិពណ៌នាវាថាជា simulated/demo payment processing មិនមែន production payment settlement។

## ស្ថាបត្យកម្ម និងការរៀបចំ Module

Order Service ត្រូវបានរៀបចំឱ្យ dependency ទៅខាងក្នុង៖

| Module/ផ្នែក | ទំនួលខុសត្រូវ |
|---|---|
| `order-domain-core` | Order aggregate, entity, value object, domain service និង business rules |
| `order-application-service` | Use cases, input/output ports និង application DTOs; source code មិន import Spring, Reactor, JPA ឬ WebClient |
| `order-service-restapi` | REST controllers, request/response DTOs, validation និង web mapping |
| `order-service-persistence` | JPA persistence និង outbound adapters សម្រាប់ការហៅ Customer/Business/Payment |
| `order-service-main` | Spring Boot startup និង dependency wiring/composition root |

REST និង persistence modules ជា outer adapters ដែល implement ports របស់ application។ Application code មិនពឹងលើ framework-specific types ទេ។ Persistence module ក៏មាន HTTP adapters ផងដែរ ដូច្នេះឈ្មោះ module នេះគ្របដណ្តប់ទាំង database និង outbound service integration; នេះជាជម្រើសរៀបចំ module បច្ចុប្បន្ន។

## របៀបដំណើរការគម្រោង

ត្រូវមាន Java 25, Gradle wrapper និង database services ដែលបានកំណត់សម្រាប់ Customer, Business និង Order។ ចាប់ផ្តើមតាមលំដាប់៖

```powershell
.\gradlew.bat :microservices:discovery-service:bootRun
.\gradlew.bat :microservices:customer-service:customer-service-main:bootRun
.\gradlew.bat :microservices:business-service:business-service-main:bootRun
.\gradlew.bat :microservices:payment-service:payment-service-main:bootRun
.\gradlew.bat :microservices:order-service:order-service-main:bootRun
```

ដំណើរការ service នីមួយៗក្នុង terminal ដាច់ដោយឡែក។ បន្ទាប់មកពិនិត្យ Eureka dashboard នៅ `http://localhost:8761` និង health endpoints នៅ `/actuator/health` តាម port របស់ service។

Build និង test command:

```powershell
.\gradlew.bat :microservices:discovery-service:test :microservices:discovery-service:bootJar
.\gradlew.bat :microservices:customer-service:customer-service-main:test :microservices:customer-service:customer-service-main:bootJar
.\gradlew.bat :microservices:business-service:business-service-main:test :microservices:business-service:business-service-main:bootJar
.\gradlew.bat :microservices:payment-service:payment-service-main:test :microservices:payment-service:payment-service-main:bootJar
.\gradlew.bat :microservices:order-service:order-service-main:test :microservices:order-service:order-service-main:bootJar
```

Build និង `bootJar` បានជោគជ័យសម្រាប់ services ទាំងប្រាំ។ បច្ចុប្បន្ន Gradle បង្ហាញ `NO-SOURCE` សម្រាប់ test tasks មានន័យថា module ទាំងនោះមិនទាន់មាន automated test source។ Runtime API tests ដែលបានរៀបរាប់ខាងលើគឺ manual integration/smoke tests។

## សេចក្តីសន្និដ្ឋាន

ការសាកល្បងបង្ហាញថា Order Service អាចទាក់ទង downstream services ដោយ Hard-Coded URLs និងដោយ Eureka service names។ Order flow បានបង្កើត និងតាមដាន order បានជោគជ័យក្នុងរបៀបទាំងពីរ នៅពេល services និង database កំពុងដំណើរការ។

Eureka ជួយកាត់បន្ថយការភ្ជាប់ caller ទៅ host/port ជាក់លាក់ និងអនុញ្ញាតឱ្យ client ប្រើ service discovery និង load balancing។ ទោះជាយ៉ាងណា registry cache, heartbeat និង instance lifecycle មានពេលវេលា update ហើយ Eureka មិនជំនួស timeout, retry, circuit breaker ឬការរចនា high availability ទេ។

ការផ្ទៀងផ្ទាត់នេះមិនបានបញ្ជាក់ការចែកចាយ load រវាង Payment instances ច្រើន ឬ Eureka health-state transition ពេល database បរាជ័យទេ។ ការអះអាងទាំងនោះត្រូវការពិសោធន៍ និង screenshot បន្ថែមមុនដាក់ស្នើ។

## បញ្ជីផ្ទៀងផ្ទាត់មុនដាក់ស្នើ

- [ ] ពិនិត្យឈ្មោះអ្នករៀបចំ និង repository URL។
- [ ] បញ្ចូល screenshot ពិតនៃ hard-coded communication test។
- [ ] បញ្ចូល screenshot Eureka dashboard ដែល services កំពុង UP។
- [ ] បញ្ចូល screenshot Eureka communication test។
- [ ] បញ្ចូល screenshot POST order response និង GET tracking response។
- [ ] បើ assignment តម្រូវការ health reporting, graceful shutdown, crash detection ឬ scale-out សូមធ្វើការពិសោធន៍ទាំងនោះ និងបញ្ចូលតែលទ្ធផលពិត។
- [ ] ដកសញ្ញាណែនាំដាក់ screenshot ចេញក្រោយពេលបញ្ចូលរូបភាព។
- [ ] មិនត្រូវអះអាងថាមាន automated tests ឬ payment settlement ពិតទេ។
- [ ] មុនបោះពុម្ព repository ជាសាធារណៈ សូមពិនិត្យកុំឱ្យមាន database credentials ឬ secrets នៅក្នុង source/configuration។

## ឯកសារយោង

1. Spring Cloud Netflix, Eureka Client registration: https://docs.spring.io/spring-cloud-netflix/reference/spring-cloud-netflix.html
2. Spring Cloud Commons, Spring Cloud LoadBalancer: https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html
3. Eureka Wiki, Netflix OSS: https://github.com/Netflix/eureka/wiki
4. Spring Boot Actuator: https://docs.spring.io/spring-boot/reference/actuator/
