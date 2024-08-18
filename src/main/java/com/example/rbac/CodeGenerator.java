//package com.example.rbac;
//
//
//import com.baomidou.mybatisplus.core.enums.SqlLike;
//import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
//import com.baomidou.mybatisplus.core.toolkit.StringPool;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.po.LikeTable;
//import com.baomidou.mybatisplus.generator.config.po.TableInfo;
//import com.baomidou.mybatisplus.generator.config.rules.DateType;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * 代码生成器
// *
// * @author KAIMAO.LONG
// * @create 2021-08-12
// */
//public class CodeGenerator {
//    private static final String PROJECT_PATH = System.getProperty("user.dir");
//
//    private static final String IP = "127.0.0.1";
//    private static final String PORT = "3306";
//    private static final String DATABASE_NAME = "my_rbac";
//    private static final String USER_NAME = "root";
//    private static final String PASSWORD = "root";
//
//    private static final String TABLE_NAME = "sys_dept,sys_job,sys_menu,sys_roles_depts,sys_roles_menus,sys_user,sys_users_jobs,sys_users_roles";
////            "pc_charging_rule,"+
////            "pc_accounting_type," +
////            "pc_data_center," +
////            "pc_data_center_instance," +
////            "pc_server_data_center_relation," +
////            "pc_goods," +
////            "pc_goods_category_relation," +
////            "pc_goods_item," +
////            "pc_product," +
////            "pc_product_spec," +
////            "pc_product_spec_attr," +
////            "pc_product_spec_relation," +
////            "pc_shop_goods_category," +
////            "pc_shop," +
////            "pc_server";
//
//
//    public static void main(String[] args) {
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 全局配置
//        mpg.setGlobalConfig(getGlobalConfig());
//
//        // 数据源配置
//        mpg.setDataSource(getDataSourceConfig());
//
//        // 包配置
//        mpg.setPackageInfo(getPackageConfig());
//
//        // 自定义输出配置
//        mpg.setCfg(getInjectionConfig());
//
//        mpg.setTemplate(getTemplateConfig());
//
//        mpg.setStrategy(getStrategyConfig());
//
//        mpg.setTemplateEngine(new VelocityTemplateEngine());
//        mpg.execute();
//
//
//    }
//
//    /**
//     * <p>
//     * 读取控制台内容
//     * </p>
//     */
//    public static String getTableName(String tip) {
//        if (org.springframework.util.StringUtils.hasText(TABLE_NAME)) {
//            return TABLE_NAME;
//        }
//        Scanner scanner = new Scanner(System.in);
//        StringBuilder help = new StringBuilder();
//        help.append("请输入" + tip + "：");
//        System.out.println(help.toString());
//        if (scanner.hasNext()) {
//            String ipt = scanner.next();
//            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(ipt)) {
//                return ipt;
//            }
//        }
//        throw new MybatisPlusException("请输入正确的" + tip + "！");
//    }
//
//
//    private static StrategyConfig getStrategyConfig() {
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setEntityLombokModel(true);
//        strategy.setRestControllerStyle(false);
//        // 公共父类
//        strategy.setSuperEntityClass(BaseEntity.class);
//
//        strategy.setTablePrefix("sys_");
//        // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
//        // 写于父类中的公共字段 strategy.setSuperEntityColumns("id");
//        strategy.setInclude(getTableName("表名").split(","));
//        // 暂时使用 setLikeTable strategy.setTablePrefix(pc.getModuleName() + "_");
//        strategy.setLikeTable(new LikeTable("sys_", SqlLike.RIGHT));
//        strategy.setControllerMappingHyphenStyle(false);
//        return strategy;
//    }
//
//    private static TemplateConfig getTemplateConfig() {
//        // 配置模板
//        TemplateConfig templateConfig = new TemplateConfig();
//        // 配置自定义输出模板
//        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
//        // templateConfig.setEntity("templates/entity2.java");
//        // templateConfig.setService();
//        templateConfig.setController("");
//
//        templateConfig.setXml(null);
//        return templateConfig;
//    }
//
//    private static InjectionConfig getInjectionConfig() {
//        // 自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//
//        // 如果模板引擎是 freemarker
////        String templatePath = "/templates/mapper.xml.ftl";
//        // 如果模板引擎是 velocity
//        String templatePath = "/templates/mapper.xml.vm";
//
//        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
//        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return PROJECT_PATH + "/rbac/src/main/resources/mapper/" + tableInfo.getMapperName() + StringPool.DOT_XML;
//
//            }
//        });
//
//        cfg.setFileOutConfigList(focList);
//        return cfg;
//    }
//
//    private static PackageConfig getPackageConfig() {
//        PackageConfig pc = new PackageConfig();
//        pc.setParent("com.example.rbac.sys");
//        pc.setService("service");
//        pc.setServiceImpl("service.impl");
//        return pc;
//    }
//
//    private static GlobalConfig getGlobalConfig() {
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setOutputDir(PROJECT_PATH + "/rbac/src/main/java");
//        globalConfig.setAuthor("MyBatis-plus generator");
//        globalConfig.setOpen(false);
//        globalConfig.setEntityName("%sEntity");
//        globalConfig.setServiceName("I%sService");
//        globalConfig.setServiceImplName("%sServiceImpl");
//        globalConfig.setDateType(DateType.ONLY_DATE);
//        return globalConfig;
//    }
//
//    private static DataSourceConfig getDataSourceConfig() {
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE_NAME + "?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC");
//        // dsc.setSchemaName("public");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername(USER_NAME);
//        dsc.setPassword(PASSWORD);
//        return dsc;
//    }
//
//}