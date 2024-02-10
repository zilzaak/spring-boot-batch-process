package com.batch.example.BootBatchExample.config;


import com.batch.example.BootBatchExample.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {


    @Bean
    public Job jobBean(JobRepository jobRepository,
                       JobCompletionNotificationImpl listener ,
                       Step steps ){


        return  new JobBuilder("job", jobRepository )
                .listener(listener)
                .start(steps)
                .build();
    }



    @Bean
    public ItemWriter<Product> itemWriter(DataSource dataSource){
        return  new JdbcBatchItemWriterBuilder<Product>()
                .sql("insert into products(product_id,title,description,discount,discounted_price)" +
                        " values(:productId,:title,:description,:discount,:discountedPrice)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Step steps(JobRepository jobRepository ,
                      DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Product> reader,
                      ItemProcessor<Product,Product> processor,
                      ItemWriter<Product> writer){

        return  new StepBuilder("jobStep",jobRepository)
                .<Product,Product>chunk(5,transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Product> reader (){

          return new FlatFileItemReaderBuilder<Product>()
                  .name("itemReader")
                  .resource(new ClassPathResource("record.csv"))
                  .delimited()
                  .names("product_id","title","description","price","discount")
                  .targetType(Product.class)
                  .build();
    }


    @Bean
    public ItemProcessor<Product,Product> itemProcessor(){

            return  new CustomItemProcessor();
    }



}
