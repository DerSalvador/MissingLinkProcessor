<#assign aDateTime = .now>
<?xml version="1.0" encoding="UTF-8"?>
<udm.DeploymentPackage version="${version!'Version missing'}"
                       application="${APPLICATION_PATH!'XLDeploy Application Path missing'}">
<deployables>
<#assign i = 1>
<#list deployables as deployable>
   <#assign aDateTimeUUID = .now>
   ${deployable.extraSteps!"<!-- No extra steps defined -->"}
   <!--${deployable.type} file="${deployable.name}/${deployable.fileName}" name="${deployable.name}-${aDateTimeUUID?string['yyyyMMdd_HHmmsss']}-${i}"-->
   <${deployable.type} file="${deployable.name}/${deployable.fileName}" name="${deployable.name}">
      <tags>
         <value>${deployable.tag!"tag missing"}</value>
         <value>Imported on ${aDateTime?string['yyyyMMdd HHmm']}</value>
      </tags>
      <checksum>${aDateTimeUUID?string['yyyyMMdd_HHmmsss']}-${i}</checksum>
      <#if deployable.scanPlaceholders??>
         <scanPlaceholders>${deployable.scanPlaceholders?c}</scanPlaceholders>
      <#else>
         <scanPlaceholders>false</scanPlaceholders>
      </#if>
      <#if deployable.type =='file.File'>
         <targetPath>${deployable.targetPath!"TargetPath missing"}</targetPath>
         <#if deployable.targetPathShared??>
	    <targetPathShared>${deployable.targetPathShared?c}</targetPathShared>
         <#else>
	    <targetPathShared>targetPathShared missing...</targetPathShared>
         </#if>
         <#if deployable.createTargetPath??>
	    <createTargetPath>${deployable.createTargetPath?c}</createTargetPath>
         <#else>
	     <createTargetPath>createTargetPath missing...</createTargetPath>
	 </#if>
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
   <#assign i = i + 1>
</#list>
<#if configurationSetPerDeployable??>
   <#assign i = 1>
   <#list configurationSetPerDeployable as configurationSet>
      <#list configurationSet.files as file>
	 <#assign aDateTimeUUID = .now>
	 <file.File name="${file.displayFileName}-${aDateTimeUUID?string['yyyyMMdd_HHmmsss']}-${i}" file="${file.name}/${file.name}">
	       <!--file.File name="${file.displayFileName}" file="${file.name}/${file.name}"-->
	       <targetPath>${file.targetPath!"targetPath missing"}</targetPath>
	       <tags>
		  <value>${tag!"tag missing"}</value>
		  <value>Imported on ${aDateTime?string['yyyyMMdd HHmm']}</value>
	       </tags>
	       <#if file.targetPathShared??>
		  <targetPathShared>${file.targetPathShared?c}</targetPathShared>
	       <#else>
		  <targetPathShared>targetPathShared missing...</targetPathShared>
	       </#if>
	       <#if file.createTargetPath??>
		  <createTargetPath>${file.createTargetPath?c}</createTargetPath>
	       <#else>
		  <createTargetPath>true</createTargetPath>
	       </#if>
	       <#if file.scanPlaceholders??>
		  <scanPlaceholders>${file.scanPlaceholders?c}</scanPlaceholders>
	       <#else>
		  <scanPlaceholders>false</scanPlaceholders>
	       </#if>
	       <targetFileName>${file.targetFileName!"targetFileName missing"}</targetFileName>
	 </file.File>
	 <#assign i = i + 1>
      </#list>
   </#list>
</#if>
</deployables>
</udm.DeploymentPackage>
