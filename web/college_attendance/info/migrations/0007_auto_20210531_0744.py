# Generated by Django 3.1.7 on 2021-05-30 23:44

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('info', '0006_auto_20210530_1907'),
    ]

    operations = [
        migrations.CreateModel(
            name='espDataDaily',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('numID', models.PositiveIntegerField()),
                ('course', models.CharField(default='None', max_length=100)),
                ('pings', models.PositiveIntegerField()),
            ],
        ),
        migrations.AlterField(
            model_name='espdata',
            name='course',
            field=models.CharField(default='None', max_length=100),
        ),
    ]