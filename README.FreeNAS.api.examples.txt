
version
-------

GET /api/v1.0/system/version/ HTTP/1.1
Content-Type: application/json

HTTP/1.1 200 OK
Vary: Accept
Content-Type: application/json

  {
          "fullversion": "FreeNAS-9.2.2-ALPHA-a346239-x64",
          "name": "FreeNAS",
          "version": "9.2.2-ALPHA"
  }


alert
-----

GET /api/v1.0/system/alert/ HTTP/1.1
Content-Type: application/json

Vary: Accept
Content-Type: application/json


{
    "meta": {
        "limit": 20,
        "next": null,
        "offset": 0,
        "previous": null,
        "total_count": 1
    },
    "objects": [
        {
            "dismissed": false,
            "id": "A;VolumeStatus;[\"Pool %(volume)s state is %(state)s: %(status)s\", {\"state\": \"DEGRADED\", \"status\": \"One or more devices has been removed by the administrator. Sufficient replicas exist for the pool to continue functioning in a degraded state.\", \"volume\": \"PoolData01\"}]",
            "level": "CRITICAL",
            "message": "Pool PoolData01 state is DEGRADED: One or more devices has been removed by the administrator. Sufficient replicas exist for the pool to continue functioning in a degraded state.",
            "timestamp": 1577122877
        }
    ]
}

or

  [{
          "id": "256ad2f48e5e541e28388701e34409cc",
          "level": "OK",
          "message": "The volume tank (ZFS) status is HEALTHY",
          "dismissed": false
  }]
