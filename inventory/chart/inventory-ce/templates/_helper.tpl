{{- define "messageHubBinding" -}}
  {{- if (index .Values "bluemix-messagehub").enabled -}}
        - name: messagehub
          valueFrom:
            secretKeyRef:
              name: {{ cat "binding-" (( index .Values "bluemix-messagehub").service.name | lower | replace " " "-") | nospace }}
              key: binding
  {{- end -}}
{{- end -}}

{{- define "mysqlBindingName" -}}
  {{- if (index .Values "bluemix-compose-mysql").enabled -}}
    {{- cat "binding-" ((index .Values "bluemix-compose-mysql").service.name | lower | replace " " "-") | nospace -}}
  {{- else -}}
    {{- (index .Values "ibmcase-mysql").binding.name -}}
  {{- end -}}
{{- end -}}
