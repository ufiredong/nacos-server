# nacos-server  注册中心  服务注册与发现
## [demo](http://nacos.ufiredong.cn/)
## ServiceStatusListner 监听器
    /**
     * @description: 服务监听器
     * @author: fengandong
     * @time: 2020/12/31 23:40
     */
    @Component
    public class ServiceStatusListner {
        private static Logger logger = LoggerFactory.getLogger(ServiceStatusListner.class);
        @Autowired
        private RedisTemplate redisTemplate;
        @Autowired
        private NamingService namingService;
        private final String SERVICE_NAME = "ufire-websocket";
    
        //初始化监听服务上下线
        @PostConstruct
        public void init() throws Exception {
            // 每次ufire-websocket实例发生上线事件即更新redis
            namingService.subscribe(SERVICE_NAME, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    List<Instance> instances = ((NamingEvent) event).getInstances();
                    redisTemplate.convertAndSend(SERVICE_NAME, JSON.toJSONString(instances));
                    System.out.println("监听到服务:" + SERVICE_NAME + " 发生变动" + JSON.toJSONString(instances));
                }
            });
        }
    
        @Bean
        public NamingService getNamingService() throws NacosException {
            Properties properties = System.getProperties();
            properties.setProperty("serverAddr", "127.0.0.1:8848");
            properties.setProperty("namespace", "public");
            NamingService naming = NamingFactory.createNamingService(properties);
            return naming;
        }
    
    

