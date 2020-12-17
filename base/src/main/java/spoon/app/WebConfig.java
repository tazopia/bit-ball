package spoon.app;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import spoon.config.domain.Config;
import spoon.support.interceptor.BlockInterceptor;
import spoon.support.interceptor.UserPostInterceptor;

import java.util.List;

@AllArgsConstructor
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private UserPostInterceptor userPostInterceptor;

    private BlockInterceptor blockInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userPostInterceptor)
                .excludePathPatterns("/*", Config.getPathAdmin(), Config.getPathSeller())
                .addPathPatterns(Config.getPathSite() + "/**", Config.getPathAdmin() + "/**", Config.getPathSeller() + "/**");
        registry.addInterceptor(blockInterceptor)
                .excludePathPatterns(Config.getPathAdmin() + "/**")
                .addPathPatterns("/**/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setOneIndexedParameters(true);
        argumentResolvers.add(pageableResolver);
        super.addArgumentResolvers(argumentResolvers);
    }

}
