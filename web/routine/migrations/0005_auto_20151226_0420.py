# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2015-12-26 04:20
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0004_auto_20151212_1432'),
    ]

    operations = [
        migrations.AlterField(
            model_name='period',
            name='teachers',
            field=models.TextField(),
        ),
    ]
