{{- define "messageHubBinding" -}}
  {{- if .Values.messagehub.binding.name -}}
    {{- .Values.messagehub.binding.name -}}
  {{- end -}}
{{- end -}}

{{- define "mysqlBindingName" -}}
  {{- .Values.mysql.binding.name -}}
{{- end -}}

{{- define "inventoryDockerImage" -}}
  {{- .Values.image.repository }}
{{- end -}}

{{- define "dataLoaderDockerImage" -}}
  {{- .Values.dataloader.image.repository }}
{{- end -}}
