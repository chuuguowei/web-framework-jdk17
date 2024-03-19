package org.example.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {
        "org.example.dao.mapper"}, sqlSessionFactoryRef = "mybatisSqlSessionFactory")
public class MyBatisPlusConfig {

    @Bean(name = "plus")
    @ConfigurationProperties(prefix = "spring.datasource.plus")
    public DataSource businessDbDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 生成mybatis sqlSessionFactory Bean对象。
     *
     * @param dataSource 数据源
     * @return 返回mybatis SqlSessionFactory Bean对象
     * @throws Exception 异常
     */
    @Bean
    @DependsOnDatabaseInitialization
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactory(@Qualifier("plus") DataSource dataSource)
    throws Exception {

        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/plus/*.xml"));
        return bean;
    }

}