def call(NAMESPACE, APPNAME, SERVICEPORT, ENVIRONMENT, SECRET, HOST)
{
        //Roll out to Dev Environment
          sh '''
                cd /home/svc_mwadmin/ucp
                ls -ltr
                eval "$(cat env.sh)"
                kubectl get po -n $NAMESPACE
                cd -

                sed -i.bak "s/{{ NAMESPACE }}/$NAMESPACE/g" ./kube/$ENVIRONMENT/*.yaml
                sed -i.bak "s/{{ APPNAME }}/$APPNAME/g" ./kube/$ENVIRONMENT/*.yaml
                sed -i.bak "s/{{ ENVIRONMENT }}/$ENVIRONMENT/g" ./kube/$ENVIRONMENT/*.yaml
                sed -i.bak "s/{{ SERVICEPORT }}/$SERVICEPORT/g" ./kube/$ENVIRONMENT/*.yaml
                sed -i.bak "s/{{ SECRET }}/$SECRET/g" ./kube/$ENVIRONMENT/*ingress.yaml
                sed -i.bak "s/{{ HOST }}/$HOST/g" ./kube/$ENVIRONMENT/*ingress.yaml

		kubectl --namespace=$NAMESPACE create cm $APPNAME-server-conf --from-file=conf-qa.properties --from-file=conf.properties -o yaml --dry-run | kubectl apply -f -
		kubectl --namespace=$NAMESPACE create cm $APPNAME-servers-conf --from-file=ccutility_7.4.0.properties -o yaml --dry-run | kubectl apply -f -
                kubectl --namespace=$NAMESPACE apply -f kube/$ENVIRONMENT/app-deploy.yaml
                kubectl --namespace=$NAMESPACE apply -f kube/$ENVIRONMENT/app-service.yaml
                if ! kubectl get ing $APPNAME  > /dev/null; then kubectl --namespace=$NAMESPACE apply -f kube/$ENVIRONMENT/app-ingress.yaml; else echo "Ingress $APPNAME already exist, Skipping for now !"; fi
    '''
}
