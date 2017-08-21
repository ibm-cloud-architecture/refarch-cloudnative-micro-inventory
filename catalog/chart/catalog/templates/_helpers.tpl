{{- define "elasticsearchBindingName" -}}
  {{- if (index .Values "bluemix-compose-elasticsearch").enabled -}}
    {{- cat "binding-" ((index .Values "bluemix-compose-elasticsearch").service.name | lower | replace " " "-") | nospace -}}
  {{- else if .Values.tags.bluemix -}}
    {{- cat "binding-" ((index .Values "bluemix-compose-elasticsearch").service.name | lower | replace " " "-") | nospace -}}
  {{- else -}}
    {{- (index .Values "ibmcase-elasticsearch").secretName -}}
  {{- end -}}
{{- end -}}

{{- define "inventoryServiceUrl" -}}
  {{- if .Values.inventory.service.url -}}
    {{ .Values.service.inventory.url }}
  {{- else -}}
    {{/* assume one is installed with release */}}
    {{- printf "http://%s-inventory:8080" .Release.Name -}}
  {{- end }}
{{- end -}}
