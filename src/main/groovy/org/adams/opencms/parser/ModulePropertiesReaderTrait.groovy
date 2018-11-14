package org.adams.opencms.parser

import org.adams.opencms.beans.Info
import org.adams.opencms.beans.Module
import org.adams.opencms.utils.FormatUtils

trait ModulePropertiesReaderTrait {

    public Module parseModuleProperties(File modulePropertiesFile) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(modulePropertiesFile))

        Module module = new Module()
        module.name = properties.get("module.name")
        module.authorname = properties.get("module.authorname")
        module.authoremail = properties.get("module.authoremail")
        module.exportMode = properties.get("module.export-mode")
        module.clazz = properties.get("module.class")
        module.description = properties.get("module.description")
        module.nicename = properties.get("module.nicename")
        module.group = properties.get("module.group")
        module.version = properties.get("module.version")
        module.dateCreated = FormatUtils.simpleFormatter.parse(properties.get("module.datecreated"))
        module.dateInstalled = FormatUtils.simpleFormatter.parse(properties.get("module.dateinstalled"))
        module.userInstalled = properties.get("module.userinstalled")
        return module
    }

    public Info parseInfoProperties(File modulePropertiesFile) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(modulePropertiesFile))
        Info info = new Info()
        info.info_project = properties.get("info.infoproject")
        info.opencms_version = properties.get("info.opencms_version")
        info.export_version = properties.get("info.export_version")
        info.creator = properties.get("info.creator")
        info.createDate = FormatUtils.simpleFormatter.parse(properties.get("info.createdate"))
        return info
    }
}