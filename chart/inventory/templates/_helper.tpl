{{- define "inventory.fullname" -}}
  {{- if .Values.fullnameOverride -}}
    {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
  {{- else -}}
    {{- printf "%s-%s" .Release.Name .Chart.Name -}}
  {{- end -}}
{{- end -}}

{{/* MySQL Init Container Template */}}
{{- define "inventory.labels" }}
{{- range $key, $value := .Values.labels }}
{{ $key }}: {{ $value | quote }}
{{- end }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{/* MySQL Init Container Template */}}
{{- define "inventory.mysql.initcontainer" }}
{{- if not (or .Values.global.istio.enabled .Values.istio.enabled) }}
- name: test-mysql
  image: {{ .Values.mysql.image }}:{{ .Values.mysql.imageTag }}
  imagePullPolicy: {{ .Values.mysql.imagePullPolicy }}
  command:
  - "/bin/bash"
  - "-c"
  {{- if or .Values.mysql.password .Values.mysql.existingSecret }}
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -e status; do echo waiting for mysql; sleep 1; done"
  {{- else }}
  - "until mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u${MYSQL_USER} -e status; do echo waiting for mysql; sleep 1; done"
  {{- end }}
  env:
  {{- include "inventory.mysql.environmentvariables" . | indent 2 }}
{{- end }}
{{- end }}

{{/* Inventory MySQL Environment Variables */}}
{{- define "inventory.mysql.environmentvariables" }}
- name: MYSQL_HOST
  value: {{ .Values.mysql.host | quote }}
- name: MYSQL_PORT
  value: {{ .Values.mysql.port | quote }}
- name: MYSQL_DATABASE
  value: {{ .Values.mysql.database | quote }}
- name: MYSQL_USER
  value: {{ .Values.mysql.user | quote }}
{{- if or .Values.mysql.password .Values.mysql.existingSecret }}
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ template "inventory.mysql.secretName" . }}
      key: mysql-password
{{- end }}
{{- end }}

{{/* Inventory MySQL Secret Name */}}
{{- define "inventory.mysql.secretName" }}
  {{- if .Values.mysql.existingSecret }}
    {{- .Values.mysql.existingSecret }}
  {{- else -}}
    {{ template "inventory.fullname" . }}-mysql-secret
  {{- end }}
{{- end }}