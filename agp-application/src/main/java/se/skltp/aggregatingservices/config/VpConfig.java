package se.skltp.aggregatingservices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "vp")
public class VpConfig {

	// String host;
	// int port;
	// String address;
	// String findcontent.address;
	// boolean disconnect=false;
	// boolean keepAlive=true;
	// int connect.timeout=2000
	String instanceId;
}
