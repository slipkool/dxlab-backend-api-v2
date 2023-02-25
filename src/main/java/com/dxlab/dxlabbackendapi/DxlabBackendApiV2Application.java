package com.dxlab.dxlabbackendapi;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class DxlabBackendApiV2Application {

	public static void main(String[] args) {
		SpringApplication.run(DxlabBackendApiV2Application.class, args);
	}

}
