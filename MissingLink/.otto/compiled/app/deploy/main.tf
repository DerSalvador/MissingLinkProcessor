# Generated by Otto, do not edit manually

provider "aws" {
    access_key = "${var.aws_access_key}"
    secret_key = "${var.aws_secret_key}"
    region = "${var.aws_region}"
}

# Deploy a set of instances
resource "aws_instance" "MissingLink" {
    ami = "${var.ami}"
    instance_type = "${var.instance_type}"
    subnet_id = "${var.subnet_id}"

    tags {
        Name = "MissingLink"
    }
}
