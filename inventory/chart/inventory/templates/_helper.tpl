{{- define "inventoryServiceName" -}}
  {{- .Release.Name }}-{{ .Values.service.name -}}
{{- end -}}

{{- define "messageHubBinding" -}}
  {{- if .Values.messagehub.binding.name -}}
    {{- .Values.messagehub.binding.name -}}
  {{- end -}}
{{- end -}}

{{- define "inventoryMySQLBindingName" -}}
  {{- .Values.inventorymysql.binding.name -}}
{{- end -}}
