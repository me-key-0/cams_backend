# Service Discovery

The Service Discovery service provides centralized service registration and discovery using Netflix Eureka for the CAMS system.

## 🎯 Purpose

- Service registration and discovery
- Load balancing support
- Health monitoring
- Service location transparency
- Fault tolerance

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+

### Configuration

```yaml
server:
  port: 8761

spring:
  application:
    name: service-discovery

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Running the Service

```bash
cd service-discovery
mvn spring-boot:run
```

## 🌐 Eureka Dashboard

Once the service is running, you can access the Eureka Dashboard at:

**URL:** `http://localhost:8761`

The dashboard provides:
- List of registered services
- Service instance details
- Health status
- Service metadata

## 📡 Service Registration

All CAMS services automatically register with Eureka using the following configuration:

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.value}
```

## 🏗️ Registered Services

The following services register with Eureka:

| Service Name | Port | Status |
|--------------|------|--------|
| api-gateway | 8760 | UP |
| auth-service | 8762 | UP |
| user-service | 8763 | UP |
| course-service | 8764 | UP |
| communication-service | 8765 | UP |
| grade-service | 8766 | UP |
| resource-service | 8767 | UP |

## 🔧 Features

### Service Discovery

- Automatic service registration
- Dynamic service discovery
- Load balancing support
- Health check integration

### High Availability

- Multiple instance support
- Failover capabilities
- Self-healing architecture
- Graceful degradation

### Monitoring

- Real-time service status
- Instance health monitoring
- Service metadata tracking
- Performance metrics

## 📊 Health Monitoring

Eureka continuously monitors registered services:

- **UP**: Service is healthy and available
- **DOWN**: Service is unavailable
- **OUT_OF_SERVICE**: Service is temporarily unavailable
- **UNKNOWN**: Service status cannot be determined

## 🔍 API Endpoints

### Service Registry Information

#### Get All Services
**GET** `/eureka/apps`

**Response:** XML format with all registered services

#### Get Specific Service
**GET** `/eureka/apps/{serviceName}`

**Response:** XML format with service instances

#### Service Instance Information
**GET** `/eureka/apps/{serviceName}/{instanceId}`

**Response:** XML format with instance details

### Health Check

#### Eureka Health
**GET** `/actuator/health`

**Response:**
```json
{
  "status": "UP"
}
```

## 🧪 Testing

### Verify Service Registration

```bash
# Check if services are registered
curl -X GET http://localhost:8761/eureka/apps \
  -H "Accept: application/json"

# Check specific service
curl -X GET http://localhost:8761/eureka/apps/auth-service \
  -H "Accept: application/json"
```

### Service Discovery Testing

```bash
# Test service discovery from another service
curl -X GET http://localhost:8760/actuator/health

# Verify API Gateway can discover services
curl -X GET http://localhost:8760/api/auth/health
```

## 🔐 Security Considerations

### Production Deployment

For production environments, consider:

- Enable Eureka security
- Use HTTPS for service communication
- Implement proper authentication
- Network-level security (VPC, firewalls)

### Configuration Example

```yaml
eureka:
  server:
    enable-self-preservation: false
  client:
    register-with-eureka: false
    fetch-registry: false
security:
  basic:
    enabled: true
  user:
    name: admin
    password: ${EUREKA_PASSWORD}
```

## 📈 Performance Tuning

### Eureka Configuration

```yaml
eureka:
  server:
    # Disable self-preservation in development
    enable-self-preservation: false
    # Eviction interval
    eviction-interval-timer-in-ms: 15000
  instance:
    # Lease renewal interval
    lease-renewal-interval-in-seconds: 10
    # Lease expiration duration
    lease-expiration-duration-in-seconds: 30
```

### Client Configuration

```yaml
eureka:
  client:
    # Registry fetch interval
    registry-fetch-interval-seconds: 10
    # Instance info replication interval
    instance-info-replication-interval-seconds: 10
```

## 🚨 Troubleshooting

### Common Issues

1. **Services Not Registering**
   - Check Eureka server is running
   - Verify network connectivity
   - Check service configuration

2. **Service Discovery Fails**
   - Verify service names match
   - Check Eureka client configuration
   - Ensure services are UP

3. **Slow Service Discovery**
   - Adjust registry fetch intervals
   - Check network latency
   - Optimize Eureka configuration

### Debug Logging

```yaml
logging:
  level:
    com.netflix.eureka: DEBUG
    com.netflix.discovery: DEBUG
```

## 📊 Monitoring and Metrics

### Eureka Metrics

Available at `/actuator/metrics`:

- `eureka.server.registry.size`
- `eureka.server.renewals`
- `eureka.server.cancellations`

### Custom Monitoring

```bash
# Monitor service count
curl -s http://localhost:8761/eureka/apps | grep -o '<application>' | wc -l

# Check service health
curl -s http://localhost:8761/actuator/health
```

## 🔄 High Availability Setup

### Multiple Eureka Instances

For production, run multiple Eureka instances:

```yaml
# Eureka Instance 1
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka2:8761/eureka/,http://eureka3:8761/eureka/

# Eureka Instance 2
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka1:8761/eureka/,http://eureka3:8761/eureka/
```

### Client Configuration for HA

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/,http://eureka3:8761/eureka/
```

## 📝 Best Practices

1. **Service Naming**: Use consistent, descriptive service names
2. **Health Checks**: Implement proper health check endpoints
3. **Graceful Shutdown**: Ensure services deregister properly
4. **Monitoring**: Monitor Eureka server health and metrics
5. **Security**: Secure Eureka in production environments
6. **Backup**: Regular backup of Eureka configuration
7. **Documentation**: Keep service registry documentation updated

## 🔧 Configuration Reference

### Server Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8761 | Eureka server port |
| `eureka.server.enable-self-preservation` | true | Self-preservation mode |
| `eureka.server.eviction-interval-timer-in-ms` | 60000 | Eviction interval |

### Client Properties

| Property | Default | Description |
|----------|---------|-------------|
| `eureka.client.register-with-eureka` | true | Register with Eureka |
| `eureka.client.fetch-registry` | true | Fetch registry |
| `eureka.client.registry-fetch-interval-seconds` | 30 | Fetch interval |

## 🚀 Future Enhancements

- Service mesh integration (Istio, Linkerd)
- Advanced load balancing strategies
- Circuit breaker integration
- Distributed tracing support
- Enhanced security features
- Multi-region support
- Service dependency mapping
- Automated service scaling