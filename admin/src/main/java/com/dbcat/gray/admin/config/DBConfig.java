package com.dbcat.gray.admin.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages =
        {"com.dbcat.gray.**.mapper"},
        sqlSessionTemplateRef = "officialSqlSessionTemplate")
public class DBConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.official")
    public DataSource officialDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory officialSqlSessionFactory(@Qualifier("officialDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*Mapper.xml"));
        return factoryBean.getObject();

    }

    @Bean
    public SqlSessionTemplate officialSqlSessionTemplate(@Qualifier("officialSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    DataSourceTransactionManager officialTransactionManager(@Qualifier("officialDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
