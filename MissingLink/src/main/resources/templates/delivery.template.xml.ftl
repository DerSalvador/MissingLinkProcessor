<vendorDelivery version="{{vendor-package-version}}" application="{{vendor-application-name}}">
	<contacts>
		<contact role="Business">
			<name>{{business-contact-name}}</name>
			<adress>{{business-contact-address}}</adress>
			<email>{{business-contact-email-address}}</email>
			<phone>{{business-contact-phone}}</phone>
		</contact>
		<contact role="Technical">
			<name>{{technical-contact-name}}</name>
			<adress>{{technical-contact-address}}</adress>
			<email>{{technical-contact-email-address}}</email>
			<phone>{{technical-contact-phone}}</phone>
		</contact>
	</contacts>
	<deployables>
		<j2ee.EAR id="1" groupid="{{import-group-name}}" file="{{deployable-file}}" name="{{deployable-name}}" tag="{{deployable-server-tag-name}}" version="{{deployable-version}}">
		    <!-- IMPORTANT: file="{{deployable-file}}" name="{{deployable-name}}" must match deployable={{deployable-name}} and file={{deployable-file}} attribute in deliverylocationlookup xmls 
			                In case of config file deployable-name (name attribute) must be taken from parent archive and file name from name attribute of corresponding file tag in configurationSet -->
			<configurationset>{{reference-to-configurationSet-id}}</configurationset>
			<configurationset>{{reference-to-configurationSet-id}}</configurationset>
		</j2ee.EAR>
	</deployables>
	<configurations>
		<configurationSet id="{{reference-id}}" name="{{configurationSet-name}}">
			<file id="1" file="{{archive-path}}!{{config-file}}" name="{{config-file-name}}">
				<ci key="{{property-name}}">
					<value type="String">{{property-default-value}}</value>
				</ci>
			</file>
		</configurationSet>
	</configurations>
	<changemanagement type="standard|normal|emergency">
		<affectedSystems>
			<infrastructure value="Yes|No"/>
			<infrastructure-config value="Yes|No"/>
			<application value="Yes|No"/>
			<application-config value="Yes|No"/>
		</affectedSystems>
    </changemanagement>
	<documentation>
		<releaseNotes></releaseNotes>
		<installationManual></installationManual>
	</documentation>
</vendorDelivery>
