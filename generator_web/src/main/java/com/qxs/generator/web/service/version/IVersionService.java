package com.qxs.generator.web.service.version;

import com.qxs.generator.web.model.version.Version;

public interface IVersionService {
	
	Version findVersion();
	
	String updateVersion(String version);
}
