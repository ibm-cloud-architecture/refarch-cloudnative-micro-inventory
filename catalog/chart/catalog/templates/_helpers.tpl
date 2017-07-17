{{- define "elasticsearchBindingName" -}}
  {{- if (index .Values "bluemix-compose-elasticsearch").enabled -}}
    {{- cat "binding-" ((index .Values "bluemix-compose-elasticsearch").service.name | lower | replace " " "-") | nospace -}}
  {{- else if .Values.tags.bluemix -}}
    {{- cat "binding-" ((index .Values "bluemix-compose-elasticsearch").service.name | lower | replace " " "-") | nospace -}}
  {{- else -}}
    {{- (index .Values "ibmcase-elasticsearch").secretName -}}
  {{- end -}}
{{- end -}}
