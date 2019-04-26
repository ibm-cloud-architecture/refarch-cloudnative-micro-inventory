{{- define "inventory.fullname" -}}
  {{- if .Values.fullnameOverride -}}
    {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
  {{- else -}}
    {{- printf "%s-%s" .Release.Name .Chart.Name -}}
  {{- end -}}
{{- end -}}

{{/* Inventory Init Container Template */}}
{{- define "inventory.labels" }}
{{- range $key, $value := .Values.labels }}
{{ $key }}: {{ $value | quote }}
{{- end }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{/* Inventory Environment Variables */}}
{{- define "inventory.environmentvariables" }}
- name: SERVICE_PORT
  value: {{ .Values.service.internalPort | quote }}
- name: JAVA_TMP_DIR
  value: /spring-tmp
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
  resources:
  {{- include "inventory.resources" . | indent 4 }}
  securityContext:
  {{- include "inventory.securityContext" . | indent 4 }}
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

{{/* Inventory Resources */}}
{{- define "inventory.resources" }}
limits:
  memory: {{ .Values.resources.limits.memory }}
requests:
  memory: {{ .Values.resources.requests.memory }}
{{- end }}

{{/* Inventory Security Context */}}
{{- define "inventory.securityContext" }}
{{- range $key, $value := .Values.securityContext }}
{{ $key }}: {{ $value }}
{{- end }}
{{- end }}

{{/* Istio Gateway */}}
{{- define "inventory.istio.gateway" }}
  {{- if or .Values.global.istio.gateway.name .Values.istio.gateway.enabled .Values.istio.gateway.name }}
  gateways:
  {{ if .Values.global.istio.gateway.name -}}
  - {{ .Values.global.istio.gateway.name }}
  {{- else if .Values.istio.gateway.enabled }}
  - {{ template "inventory.fullname" . }}-gateway
  {{ else if .Values.istio.gateway.name -}}
  - {{ .Values.istio.gateway.name }}
  {{ end }}
  {{- end }}
{{- end }}