<?xml version="1.0" encoding="UTF-8"?>
<udm.DeploymentPackage version="${version!'Version missing'}" application="${APPLICATION_PATH!'XLDeploy Application Path missing'}">
<deployables>
<#list deployables as deployable>
    <${deployable.type} file="${deployable.name}/${deployable.fileName}" name="${deployable.name}">
    <tags>
        <value>${deployable.tag!"tag missing"}</value>
    </tags>
    <scanPlaceholders>${deployable.scanPlaceholders?c}</scanPlaceholders>
    <#if deployable.type =='file.File'>
        <targetPath>${deployable.targetPath!"TargetPath missing"}</targetPath>
        <createTargetPath>${deployable.createTargetPath!"createTargetPath missing"}</createTargetPath>
        <targetFileName>${deployable.targetFileName!"targetFileName missing"}</targetFileName>
    </#if>
    <#if deployable.type =='file.Folder'>
        <targetPath>${deployable.targetPath!"TargetPath missing"}</targetPath>
        <#if deployable.createTargetPath??>
            <createTargetPath>${deployable.createTargetPath?c}</createTargetPath>
        <#else>
            <createTargetPath>true</createTargetPath>
        </#if>
    </#if>
</${deployable.type}>
</#list>
<#if configurationSetPerDeployable??>
    <#list configurationSetPerDeployable as configurationSet>
        <#list configurationSet.files as file>
            <file.File name="${file.displayFileName}" file="${file.name}/${file.name}">
                <targetPath>${file.targetPath!"targetPath missing"}</targetPath>
                <tags>
                    <value>${tag!"tag missing"}</value>
                </tags>
                <#if file.createTargetPath??>
                    <createTargetPath>${file.createTargetPath?c}</createTargetPath>
                <#else>
                    <createTargetPath>true</createTargetPath>
                </#if>
                <#if file.scanPlaceholders??>
                    <scanPlaceholders>${deployable.scanPlaceholders?c}</scanPlaceholders>
                <#else>
                    <scanPlaceholders>false</scanPlaceholders>
                </#if>
                <targetFileName>${file.targetFileName!"targetFileName missing"}</targetFileName>
            </file.File>
        </#list>
    </#list>
</#if>
    </deployables>
</udm.DeploymentPackage>
