// ------------------------------------------------------------------------------
// <auto-generated>
//    Generated by avrogen, version 1.10.0.0
//    Changes to this file may cause incorrect behavior and will be lost if code
//    is regenerated
// </auto-generated>
// ------------------------------------------------------------------------------
namespace Avro.Test.Specific.@return
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using Avro;
    using Avro.Specific;

    public partial class Record : ISpecificRecord
    {
        public static Schema _SCHEMA = Avro.Schema.Parse("{\"type\":\"record\",\"name\":\"Record\",\"namespace\":\"Avro.Test.Specific.return\",\"fields\"" +
                                                         ":[{\"name\":\"name\",\"type\":\"string\"}]}");
        private string _name;
        public virtual Schema Schema
        {
            get
            {
                return Record._SCHEMA;
            }
        }
        public string name
        {
            get
            {
                return this._name;
            }
            set
            {
                this._name = value;
            }
        }
        public virtual object Get(int fieldPos)
        {
            switch (fieldPos)
            {
                case 0: return this.name;
                default: throw new AvroRuntimeException("Bad index " + fieldPos + " in Get()");
            };
        }
        public virtual void Put(int fieldPos, object fieldValue)
        {
            switch (fieldPos)
            {
                case 0: this.name = (System.String)fieldValue; break;
                default: throw new AvroRuntimeException("Bad index " + fieldPos + " in Put()");
            };
        }
    }
}
