.PHONY: test
test:
	kubectl kuttl test --start-kind=false ./kuttl --config=./kuttl/kuttl-test.yaml && echo "test successfully done!" || (kubectl logs -n glasskube-operator deployments/glasskube-operator; exit 1)
